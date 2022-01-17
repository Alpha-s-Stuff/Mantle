package slimeknights.mantle.lib.mixin.common;

import slimeknights.mantle.lib.extensions.BlockParticleOptionExtensions;
import slimeknights.mantle.lib.util.MixinHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;

@Mixin(BlockParticleOption.class)
public abstract class BlockParticleOptionMixin implements BlockParticleOptionExtensions {

	private BlockPos create$pos;

	@Unique
	@Override
	public BlockParticleOption create$setPos(BlockPos pos) {
		this.create$pos = pos;
		return MixinHelper.cast(this);
	}

	@Unique
	@Override
	public BlockPos create$getPos() {
		return create$pos;
	}
}
