package slimeknights.mantle.lib.extensions;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;

public interface TerrainParticle$ProviderExtensions {
	Particle mantle$makeParticleAtPos(BlockParticleOption blockParticleData, ClientLevel clientWorld, double d, double e, double f, double g, double h, double i);

	Particle mantle$updateSprite(BlockPos pos);
}
