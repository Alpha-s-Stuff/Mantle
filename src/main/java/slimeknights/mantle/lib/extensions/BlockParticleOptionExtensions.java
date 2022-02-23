package slimeknights.mantle.lib.extensions;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;

public interface BlockParticleOptionExtensions {
	BlockParticleOption mantle$setPos(BlockPos pos);

	BlockPos mantle$getPos();
}
