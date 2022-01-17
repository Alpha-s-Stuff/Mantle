package slimeknights.mantle.lib.util;

import slimeknights.mantle.lib.mixin.accessor.ParticleAccessor;

import net.minecraft.client.particle.Particle;

public final class ParticleHelper {

	public static void setStoppedByCollision(Particle particle, boolean bool) {
		((ParticleAccessor) particle).create$stoppedByCollision(bool);
	}

	private ParticleHelper() {}
}
