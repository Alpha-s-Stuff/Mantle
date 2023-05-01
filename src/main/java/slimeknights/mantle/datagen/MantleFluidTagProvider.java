package slimeknights.mantle.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.FluidTagsProvider;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;
import slimeknights.mantle.Mantle;

import static slimeknights.mantle.datagen.MantleTags.Fluids.LAVA;
import static slimeknights.mantle.datagen.MantleTags.Fluids.WATER;

/** Provider for tags added by mantle, generally not useful for other mods */
public class MantleFluidTagProvider extends FabricTagProvider.FluidTagProvider {
  public MantleFluidTagProvider(FabricDataGenerator gen) {
    super(gen);
  }

  @Override
  protected void generateTags() {
    this.tag(WATER).add(Fluids.WATER, Fluids.FLOWING_WATER);
    this.tag(LAVA).add(Fluids.LAVA, Fluids.FLOWING_LAVA);
  }

  /*@Override
  public String getName() {
    return "Mantle Fluid Tag Provider";
  }*/
}
