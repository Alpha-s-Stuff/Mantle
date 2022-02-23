package slimeknights.mantle.lib.mixin.common;

import net.minecraft.world.level.material.Fluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import slimeknights.mantle.lib.extensions.FluidExtensions;
import slimeknights.mantle.lib.transfer.fluid.FluidAttributes;

@Mixin(Fluid.class)
public class FluidMixin implements FluidExtensions {
  @Unique
  private FluidAttributes mantleFluidAttributes;

  @Unique
  @Override
  public final FluidAttributes getAttributes() {
    if (mantleFluidAttributes == null)
      mantleFluidAttributes = createAttributes();
    return mantleFluidAttributes;
  }
}
