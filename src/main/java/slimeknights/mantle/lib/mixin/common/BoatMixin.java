package slimeknights.mantle.lib.mixin.common;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import slimeknights.mantle.lib.util.MixinHelper;

@Mixin(Boat.class)
public abstract class BoatMixin {
	// you can't capture locals in a @ModifyVariable, so we have this
	@Unique
	BlockState mantle$state;
	@Unique
	Level mantle$world;
	@Unique
	BlockPos.MutableBlockPos mantle$pos;
	@Unique
	Entity mantle$entity;

	@Inject(
			method = "getGroundFriction()F",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/level/block/Block;getFriction()F"
			),
			locals = LocalCapture.CAPTURE_FAILHARD
	)
	public void mantle$getBoatGlide(CallbackInfoReturnable<Float> cir,
									AABB AABB, AABB AABB2, int i, int j, int k,
									int l, int m, int n, VoxelShape shape, float f, int o, BlockPos.MutableBlockPos mutable,
									int p, int q, int r, int s, BlockState blockState) {
		mantle$state = blockState;
		mantle$world = MixinHelper.<Boat>cast(this).level;
		mantle$pos = mutable;
		mantle$entity = MixinHelper.<Boat>cast(this);
	}

	@ModifyVariable(
			method = "getGroundFriction()F",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/level/block/Block;getFriction()F",
					shift = At.Shift.AFTER
			)
	)
	public float mantle$setSlipperiness(float f) {
		return mantle$state.getSlipperiness(mantle$world, mantle$pos, mantle$entity);
	}
}
