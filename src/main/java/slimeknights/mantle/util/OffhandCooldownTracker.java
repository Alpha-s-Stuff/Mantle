package slimeknights.mantle.util;

import dev.onyxstudios.cca.api.v3.component.Component;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.PlayerComponent;
import lombok.RequiredArgsConstructor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import slimeknights.mantle.Mantle;
import io.github.fabricators_of_create.porting_lib.util.LazyOptional;
import slimeknights.mantle.network.MantleNetwork;
import slimeknights.mantle.network.packet.SwingArmPacket;

import javax.annotation.Nullable;
import java.util.function.Function;

/**
 * Logic to handle offhand having its own cooldown
 */
@RequiredArgsConstructor
public class OffhandCooldownTracker implements PlayerComponent<OffhandCooldownTracker>, EntityComponentInitializer {
  public static final ResourceLocation KEY = Mantle.getResource("offhand_cooldown");
  public static final Function<OffhandCooldownTracker,Float> COOLDOWN_TRACKER = OffhandCooldownTracker::getCooldown;
  private static final Function<OffhandCooldownTracker,Boolean> ATTACK_READY = OffhandCooldownTracker::isAttackReady;

  public OffhandCooldownTracker() {
    this.player = null;
  }

  /**
   * Capability instance for offhand cooldown
   */
  public static final ComponentKey<OffhandCooldownTracker> CAPABILITY = ComponentRegistry.getOrCreate(KEY, OffhandCooldownTracker.class);

  /** Registers the capability and subscribes to event listeners */
  @Override
  public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
    registry.registerForPlayers(CAPABILITY, OffhandCooldownTracker::attachCapability);
  }

  /** Registers the capability with the event bus */
  public static void register() {

  }

  /**
   * Called to add the capability handler to all players
   * @param player  Player
   */
  private static OffhandCooldownTracker attachCapability(Player player) {
      return new OffhandCooldownTracker(player);
  }

  /** Lazy optional of self for capability requirements */
  private final LazyOptional<OffhandCooldownTracker> capabilityInstance = LazyOptional.of(() -> this);
  /** Player receiving cooldowns */
  @Nullable
  private final Player player;
  /** Scale of the last cooldown */
  private int lastCooldown = 0;
  /** Time in ticks when the player can next attack for full power */
  private int attackReady = 0;

  /** Enables the cooldown tracker if above 0. Intended to be set in equipment change events, not serialized */
  private int enabled = 0;

  /** Null safe way to get the player's ticks existed */
  private int getTicksExisted() {
    if (player == null) {
      return 0;
    }
    return player.tickCount;
  }

  /** If true, the tracker is enabled despite a cooldown item not being held */
  public boolean isEnabled() {
    return enabled > 0;
  }

  /**
   * Call this method when your item causing offhand cooldown to be needed is enabled and disabled. If multiple placces call this, the tracker will automatically keep enabled until all places disable
   * @param enable  If true, enable. If false, disable
   */
  public void setEnabled(boolean enable) {
    if (enable) {
      enabled++;
    } else {
      enabled--;
    }
  }

  /**
   * Applies the given amount of cooldown
   * @param cooldown  Coolddown amount
   */
  public void applyCooldown(int cooldown) {
    this.lastCooldown = cooldown;
    this.attackReady = getTicksExisted() + cooldown;
  }

  /**
   * Returns a number from 0 to 1 denoting the current cooldown amount, akin to {@link Player#getAttackStrengthScale(float)}
   * @return  number from 0 to 1, with 1 being no cooldown
   */
  public float getCooldown() {
    int ticksExisted = getTicksExisted();
    if (ticksExisted > this.attackReady || this.lastCooldown == 0) {
      return 1.0f;
    }
    return Mth.clamp((this.lastCooldown + ticksExisted - this.attackReady) / (float) this.lastCooldown, 0f, 1f);
  }

  /**
   * Checks if we can perform another attack yet.
   * This counteracts rapid attacks via click macros, in a similar way to vanilla by limiting to once every 10 ticks
   */
  public boolean isAttackReady() {
    return getTicksExisted() + this.lastCooldown > this.attackReady;
  }


  /* Helpers */

  /**
   * Gets the offhand cooldown for the given player
   * @param player  Player
   * @return  Offhand cooldown
   */
  public static float getCooldown(Player player) {
    return CAPABILITY.maybeGet(player).map(COOLDOWN_TRACKER).orElse(1.0f);
  }

  /**
   * Applies cooldown to the given player
   * @param player  Player
   * @param cooldown  Cooldown to apply
   */
  public static void applyCooldown(Player player, int cooldown) {
    CAPABILITY.maybeGet(player).ifPresent(cap -> cap.applyCooldown(cooldown));
  }

  /**
   * Applies cooldown to the given player
   * @param player  Player
   */
  public static boolean isAttackReady(Player player) {
    return CAPABILITY.maybeGet(player).map(ATTACK_READY).orElse(true);
  }

  /**
   * Applies cooldown using attack speed
   * @param attackSpeed   Attack speed of the held item
   * @param cooldownTime  Relative cooldown time for the given source, 20 is vanilla
   */
  public static void applyCooldown(Player player, float attackSpeed, int cooldownTime) {
    applyCooldown(player, Math.round(cooldownTime / attackSpeed));
  }

  /** Swings the entities hand without resetting cooldown */
  public static void swingHand(LivingEntity entity, InteractionHand hand, boolean updateSelf) {
    if (!entity.swinging || entity.swingTime >= entity.getCurrentSwingDuration() / 2 || entity.swingTime < 0) {
      entity.swingTime = -1;
      entity.swinging = true;
      entity.swingingArm = hand;
      if (!entity.level.isClientSide) {
        SwingArmPacket packet = new SwingArmPacket(entity, hand);
        if (updateSelf) {
          MantleNetwork.INSTANCE.sendToTrackingAndSelf(packet, entity);
        } else {
          MantleNetwork.INSTANCE.sendToTracking(packet, entity);
        }
      }
    }
  }

  @Override
  public void readFromNbt(CompoundTag tag) {
    tag.putInt("attackReady", this.attackReady);
    tag.putInt("lastCooldown", this.lastCooldown);
    tag.putInt("enabled", this.enabled);
  }

  @Override
  public void writeToNbt(CompoundTag tag) {
    this.attackReady = tag.getInt("attackReady");
    this.lastCooldown = tag.getInt("lastCooldown");
    this.enabled = tag.getInt("enabled");
  }
}
