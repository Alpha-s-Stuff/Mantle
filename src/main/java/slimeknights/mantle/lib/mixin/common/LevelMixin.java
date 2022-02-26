package slimeknights.mantle.lib.mixin.common;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import org.jetbrains.annotations.Nullable;
import slimeknights.mantle.lib.block.WeakPowerCheckingBlock;
import slimeknights.mantle.lib.event.ExplosionEvents;
import slimeknights.mantle.lib.extensions.BlockStateExtensions;
import slimeknights.mantle.lib.util.MixinHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import java.util.Iterator;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(Level.class)
public abstract class LevelMixin {
	@Shadow
	public abstract BlockState getBlockState(BlockPos blockPos);

	@Inject(method = "getSignal", at = @At("RETURN"), cancellable = true)
	public void mantle$getRedstoneSignal(BlockPos blockPos, Direction direction, CallbackInfoReturnable<Integer> cir) {
		BlockState mantle$blockstate = MixinHelper.<Level>cast(this).getBlockState(blockPos);
		int mantle$i = mantle$blockstate.getSignal(MixinHelper.<Level>cast(this), blockPos, direction);

		if (mantle$blockstate.getBlock() instanceof WeakPowerCheckingBlock) {
			cir.setReturnValue(
					((WeakPowerCheckingBlock) mantle$blockstate.getBlock()).shouldCheckWeakPower(mantle$blockstate, MixinHelper.<Level>cast(this), blockPos, direction)
							? Math.max(mantle$i, MixinHelper.<Level>cast(this).getDirectSignalTo(blockPos))
							: mantle$i);
		}
	}

	@Inject(
			method = "updateNeighbourForOutputSignal",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/level/Level;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;",
					shift = At.Shift.AFTER
			),
			locals = LocalCapture.CAPTURE_FAILHARD
	)
	public void mantle$updateComparatorOutputLevel(BlockPos blockPos, Block block, CallbackInfo ci,
												   Iterator<?> var3, Direction direction, BlockPos blockPos2) {
		((BlockStateExtensions) getBlockState(blockPos2)).onNeighborChange(MixinHelper.cast(this), blockPos2, blockPos);
	}

  @Inject(
    method = "explode(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/damagesource/DamageSource;Lnet/minecraft/world/level/ExplosionDamageCalculator;DDDFZLnet/minecraft/world/level/Explosion$BlockInteraction;)Lnet/minecraft/world/level/Explosion;",
    at = @At(
      value = "INVOKE",
      target = "Lnet/minecraft/world/level/Explosion;explode()V",
      shift = At.Shift.BEFORE
    ),
    locals = LocalCapture.CAPTURE_FAILHARD
  )
  @SuppressWarnings("ALL")
  public void mantle$onStartExplosion(@Nullable Entity exploder, @Nullable DamageSource damageSource, @Nullable ExplosionDamageCalculator context, double x, double y, double z, float size, boolean causesFire, Explosion.BlockInteraction mode, CallbackInfoReturnable<Explosion> cir, Explosion explosion) {
    if(ExplosionEvents.START.invoker().onExplosionStart((Level) (Object) this, explosion)) cir.setReturnValue(explosion);
  }
}
