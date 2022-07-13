package slimeknights.mantle;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import slimeknights.mantle.datagen.MantleFluidTagProvider;
import slimeknights.mantle.datagen.MantleFluidTooltipProvider;

public class MantleData implements DataGeneratorEntrypoint {
  @Override
  public void onInitializeDataGenerator(FabricDataGenerator generator) {
      generator.addProvider(new MantleFluidTagProvider(generator));
      generator.addProvider(new MantleFluidTooltipProvider(generator));
  }
}
