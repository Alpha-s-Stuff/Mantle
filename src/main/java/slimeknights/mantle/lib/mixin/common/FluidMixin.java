package slimeknights.mantle.lib.mixin.common;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import slimeknights.mantle.lib.extensions.FluidExtensions;
import slimeknights.mantle.lib.extensions.RegistryNameProvider;
import slimeknights.mantle.lib.transfer.fluid.FluidAttributes;
import slimeknights.mantle.lib.util.MixinHelper;

@Mixin(Fluid.class)
public class FluidMixin implements FluidExtensions, RegistryNameProvider {
  @Unique
  private FluidAttributes mantleFluidAttributes;

  @Unique
  @Override
  public final FluidAttributes getAttributes() {
    if (mantleFluidAttributes == null)
      mantleFluidAttributes = createAttributes();
    return mantleFluidAttributes;
  }

  @Unique

  @Override
  public ResourceLocation getRegistryName() {
    return Registry.FLUID.getKey(MixinHelper.cast(this));
  }
}
