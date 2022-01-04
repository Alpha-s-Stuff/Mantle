package slimeknights.mantle.lib.mixin.accessor;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleProvider;

@Environment(EnvType.CLIENT)
@Mixin(ParticleEngine.class)
public interface ParticleEngineAccessor {
	@Accessor("providers")
	Int2ObjectMap<ParticleProvider<?>> create$getProviders();
}
