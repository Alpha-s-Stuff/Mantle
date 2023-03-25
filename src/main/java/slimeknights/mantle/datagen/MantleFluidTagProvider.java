package slimeknights.mantle.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.level.material.Fluids;

import java.util.concurrent.CompletableFuture;

import static slimeknights.mantle.datagen.MantleTags.Fluids.LAVA;
import static slimeknights.mantle.datagen.MantleTags.Fluids.WATER;

/** Provider for tags added by mantle, generally not useful for other mods */
public class MantleFluidTagProvider extends FabricTagProvider.FluidTagProvider {
  public MantleFluidTagProvider(FabricDataOutput gen, CompletableFuture<HolderLookup.Provider> registriesFuture) {
    super(gen, registriesFuture);
  }

  @Override
  protected void addTags(HolderLookup.Provider provider) {
    this.getOrCreateTagBuilder(WATER).add(Fluids.WATER, Fluids.FLOWING_WATER);
    this.getOrCreateTagBuilder(LAVA).add(Fluids.LAVA, Fluids.FLOWING_LAVA);
  }
}
