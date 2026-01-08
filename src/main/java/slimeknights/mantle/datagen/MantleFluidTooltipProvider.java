package slimeknights.mantle.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import org.jetbrains.annotations.ApiStatus.Internal;
import slimeknights.mantle.fluid.tooltip.AbstractFluidTooltipProvider;
import slimeknights.mantle.fluid.tooltip.FluidTooltipHandler;

/** Mantle datagen for fluid tooltips. For mods, don't use this, use {@link AbstractFluidTooltipProvider} */
@Internal
public class MantleFluidTooltipProvider extends AbstractFluidTooltipProvider {
  public MantleFluidTooltipProvider(FabricDataOutput packOutput) {
    super(packOutput);
  }

  @Override
  protected void addFluids() {
    add("buckets").addUnit("bucket", FluidConstants.BUCKET);
    addRedirect(FluidTooltipHandler.DEFAULT_ID, id("buckets"));
    // water divides into bottles then "drops"
    add("water", MantleTags.Fluids.WATER)
      .addUnit("bucket", FluidConstants.BUCKET)
      .addUnit("bottle", MantleValues.BOTTLE)
      .addUnit("drop", MantleValues.DROP);
    // potions and soup don't bother with buckets, stick with the directly useful units
    add("potion", MantleTags.Fluids.POTION)
      .addUnit("bottle", MantleValues.BOTTLE)
      .addUnit("sip", MantleValues.SIP);
    add("soup", MantleTags.Fluids.SOUP)
      .addUnit("bowl", MantleValues.BOWL)
      .addUnit("sip", MantleValues.SIP);
    // honey buckets are equal to honey blocks making it a useful number
    add("honey", MantleTags.Fluids.HONEY)
      .addUnit("bucket", MantleValues.BOTTLE * 4)
      .addUnit("bottle", MantleValues.BOTTLE)
      .addUnit("sip", MantleValues.SIP);
  }

  @Override
  public String getName() {
    return "Mantle Fluid Tooltip Provider";
  }
}
