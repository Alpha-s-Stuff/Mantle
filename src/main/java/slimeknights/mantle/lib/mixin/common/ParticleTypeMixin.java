package slimeknights.mantle.lib.mixin.common;

import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import slimeknights.mantle.lib.extensions.RegistryNameProvider;
import slimeknights.mantle.lib.util.MixinHelper;

@Mixin(ParticleType.class)
public class ParticleTypeMixin implements RegistryNameProvider {

  @Unique
  @Override
  public ResourceLocation getRegistryName() {
    return Registry.PARTICLE_TYPE.getKey(MixinHelper.cast(this));
  }
}
