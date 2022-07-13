package slimeknights.mantle.datagen;

import io.github.fabricators_of_create.porting_lib.util.FluidAttributes;
import net.minecraft.data.DataGenerator;
import slimeknights.mantle.Mantle;
import slimeknights.mantle.fluid.tooltip.AbstractFluidTooltipProvider;
import slimeknights.mantle.fluid.tooltip.FluidTooltipHandler;

/** Mantle datagen for fluid tooltips. For mods, don't use this, use {@link AbstractFluidTooltipProvider} */
public class MantleFluidTooltipProvider extends AbstractFluidTooltipProvider {
  public MantleFluidTooltipProvider(DataGenerator generator) {
    super(generator, Mantle.modId);
  }

  @Override
  protected void addFluids() {
    add("buckets")
      .addUnit("kilobucket", FluidAttributes.BUCKET_VOLUME * 1000)
      .addUnit("bucket", FluidAttributes.BUCKET_VOLUME);
    addRedirect(FluidTooltipHandler.DEFAULT_ID, id("buckets"));
  }

  @Override
  public String getName() {
    return "Mantle Fluid Tooltip Provider";
  }
}
