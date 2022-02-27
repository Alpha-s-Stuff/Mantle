package slimeknights.mantle.lib.event;

import lombok.Getter;
import lombok.Setter;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import java.util.Collection;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import slimeknights.mantle.lib.util.MantleEvent;

public class LivingEntityEvents {
	public static final Event<ExperienceDrop> EXPERIENCE_DROP = EventFactory.createArrayBacked(ExperienceDrop.class, callbacks -> (i, player) -> {
		for (ExperienceDrop callback : callbacks) {
			return callback.onLivingEntityExperienceDrop(i, player);
		}

		return i;
	});

	public static final Event<KnockBackStrength> KNOCKBACK_STRENGTH = EventFactory.createArrayBacked(KnockBackStrength.class, callbacks -> (strength, player) -> {
		for (KnockBackStrength callback : callbacks) {
			return callback.onLivingEntityTakeKnockback(strength, player);
		}

		return strength;
	});

	public static final Event<Drops> DROPS = EventFactory.createArrayBacked(Drops.class, callbacks -> (target, source, drops) -> {
		for (Drops callback : callbacks) {
			return callback.onLivingEntityDrops(target, source, drops);
		}

		return false;
	});

  public static final Event<Fall> FALL = EventFactory.createArrayBacked(Fall.class, callbacks -> fallEvent -> {
    for(Fall e : callbacks)
      e.onFall(fallEvent);
  });

	public static final Event<LootingLevel> LOOTING_LEVEL = EventFactory.createArrayBacked(LootingLevel.class, callbacks -> (source, original) -> {
		for (LootingLevel callback : callbacks) {
			int lootingLevel = callback.modifyLootingLevel(source, original);
			if (lootingLevel != 0) {
				return lootingLevel;
			}
		}

		return original;
	});

	public static final Event<Tick> TICK = EventFactory.createArrayBacked(Tick.class, callbacks -> (entity) -> {
		for (Tick callback : callbacks) {
			callback.onLivingEntityTick(entity);
		}
	});

	public static final Event<Hurt> HURT = EventFactory.createArrayBacked(Hurt.class, callbacks -> (source, amount) -> {
		for (Hurt callback : callbacks) {
			float newAmount = callback.onHurt(source, amount);
			if (newAmount != amount) return newAmount;
		}
		return amount;
	});

  public static final Event<Jump> JUMP = EventFactory.createArrayBacked(Jump.class, callbacks -> (entity) -> {
    for (Jump callback : callbacks) {
      callback.onLivingEntityJump(entity);
    }
  });

	@FunctionalInterface
	public interface Hurt {
		float onHurt(DamageSource source, float amount);
	}

  @FunctionalInterface
  public interface Fall {
    void onFall(LivingFallEvent event);
  }

  @Getter @Setter
  public static class LivingFallEvent extends MantleEvent.EntityEvent {
    private float distance, damageMultiplier;

    public LivingFallEvent(LivingEntity entity, float distance, float damageMultiplier) {
      super(entity);
      this.distance = distance;
      this.damageMultiplier = damageMultiplier;
    }

    @Override
    public void sendEvent() {
      FALL.invoker().onFall(this);
    }
  }

	@FunctionalInterface
	public interface ExperienceDrop {
		int onLivingEntityExperienceDrop(int i, Player player);
	}

	@FunctionalInterface
	public interface KnockBackStrength {
		double onLivingEntityTakeKnockback(double strength, Player player);
	}

	@FunctionalInterface
	public interface Drops {
		boolean onLivingEntityDrops(LivingEntity target, DamageSource source, Collection<ItemEntity> drops);
	}

	@FunctionalInterface
	public interface LootingLevel {
		int modifyLootingLevel(DamageSource source, int original);
	}

	@FunctionalInterface
	public interface Tick {
		void onLivingEntityTick(LivingEntity entity);
	}

  @FunctionalInterface
  public interface Jump {
    void onLivingEntityJump(LivingEntity entity);
  }
}
