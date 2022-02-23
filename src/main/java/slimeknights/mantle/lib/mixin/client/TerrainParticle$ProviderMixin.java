package slimeknights.mantle.lib.mixin.client;

import slimeknights.mantle.lib.extensions.BlockParticleOptionExtensions;
import slimeknights.mantle.lib.extensions.TerrainParticle$ProviderExtensions;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.TerrainParticle;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.world.level.block.Blocks;

@Environment(EnvType.CLIENT)
@Mixin(TerrainParticle.Provider.class)
public abstract class TerrainParticle$ProviderMixin implements TerrainParticle$ProviderExtensions {
	@Unique
	@Override
	public Particle mantle$makeParticleAtPos(BlockParticleOption blockParticleData, ClientLevel clientWorld,
											 double d, double e, double f, double g, double h, double i) {
		return !blockParticleData.getState().isAir() && !blockParticleData.getState().is(Blocks.MOVING_PISTON)
				? ((TerrainParticle$ProviderExtensions) (new TerrainParticle(clientWorld, d, e, f, g, h, i, blockParticleData.getState()))).mantle$updateSprite(((BlockParticleOptionExtensions) blockParticleData).mantle$getPos())
				: null;
	}
}
