package slimeknights.mantle.datagen;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import slimeknights.mantle.Mantle;
import slimeknights.mantle.fluid.tooltip.AbstractFluidTooltipProvider;
import slimeknights.mantle.fluid.tooltip.FluidTooltipHandler;

import java.io.IOException;

/** Mantle datagen for fluid tooltips. For mods, don't use this, use {@link AbstractFluidTooltipProvider} */
public class MantleFluidTooltipProvider extends AbstractFluidTooltipProvider {
  public MantleFluidTooltipProvider(DataGenerator generator) {
    super(generator, Mantle.modId);
  }

  @Override
  protected void addFluids() {
    add("buckets")
      .addUnit("kilobucket", FluidConstants.BUCKET * 1000)
      .addUnit("bucket", FluidConstants.BUCKET)
      .addUnit("droplet", 1);
    addRedirect(FluidTooltipHandler.DEFAULT_ID, id("buckets"));
  }

  @Override
  public String getName() {
    return "Mantle Fluid Tooltip Provider";
  }
}
