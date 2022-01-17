package slimeknights.mantle.lib.mixin.common;

import slimeknights.mantle.lib.extensions.BlockStateExtensions;
import slimeknights.mantle.lib.util.MixinHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.FlyingMob;
import net.minecraft.world.level.block.Block;

@Mixin(FlyingMob.class)
public abstract class FlyingMobMixin {
	@Redirect(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;getFriction()F"))
	public float create$setSlipperiness(Block instance) {
		FlyingMob self = MixinHelper.cast(this);
		BlockPos ground = new BlockPos(
				self.getX(),
				self.getY() - 1.0D,
				self.getZ());

		return ((BlockStateExtensions) self.level.getBlockState(ground))
				.create$getSlipperiness(self.level, ground, self) * 0.91F;
	}
}
