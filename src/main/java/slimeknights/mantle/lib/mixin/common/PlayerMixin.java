package slimeknights.mantle.lib.mixin.common;

import net.minecraft.world.damagesource.DamageSource;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
import slimeknights.mantle.lib.block.HarvestableBlock;
import slimeknights.mantle.lib.event.LivingEntityEvents;
import slimeknights.mantle.lib.event.PlayerTickEvents;
import slimeknights.mantle.lib.util.MixinHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity {
	@Final
	@Shadow
	private Inventory inventory;

	protected PlayerMixin(EntityType<? extends LivingEntity> entityType, Level level) {
		super(entityType, level);
	}

	@Inject(method = "hasCorrectToolForDrops", at = @At("HEAD"), cancellable = true)
	public void mantle$isUsingEffectiveTool(BlockState blockState, CallbackInfoReturnable<Boolean> cir) {
		if (blockState.getBlock() instanceof HarvestableBlock harvestable && inventory.getSelected().getItem() instanceof DiggerItem digger) {
			cir.setReturnValue(harvestable.isToolEffective(blockState, digger));
		}
	}

  @Inject(method = "tick", at = @At("HEAD"))
  public void mantle$clientStartOfTickEvent(CallbackInfo ci) {
    PlayerTickEvents.START.invoker().onStartOfPlayerTick(MixinHelper.cast(this));
  }

	@Inject(method = "tick", at = @At("TAIL"))
	public void mantle$clientEndOfTickEvent(CallbackInfo ci) {
		PlayerTickEvents.END.invoker().onEndOfPlayerTick(MixinHelper.cast(this));
	}

  @ModifyArgs(method = "actuallyHurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;getDamageAfterArmorAbsorb(Lnet/minecraft/world/damagesource/DamageSource;F)F"))
  private void mantle$onHurt(Args args) {
    DamageSource source = args.get(0);
    float currentAmount = args.get(1);
    float newAmount = LivingEntityEvents.ACTUALLY_HURT.invoker().onHurt(source, this, currentAmount);
    if (newAmount != currentAmount)
      args.set(1, newAmount);
  }
}
