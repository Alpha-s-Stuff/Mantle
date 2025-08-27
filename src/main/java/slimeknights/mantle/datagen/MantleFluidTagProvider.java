package slimeknights.mantle.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.ApiStatus.Internal;

import java.util.concurrent.CompletableFuture;

import static slimeknights.mantle.datagen.MantleTags.Fluids.BEETROOT_SOUP;
import static slimeknights.mantle.datagen.MantleTags.Fluids.LAVA;
import static slimeknights.mantle.datagen.MantleTags.Fluids.MUSHROOM_STEW;
import static slimeknights.mantle.datagen.MantleTags.Fluids.RABBIT_STEW;
import static slimeknights.mantle.datagen.MantleTags.Fluids.SOUP;
import static slimeknights.mantle.datagen.MantleTags.Fluids.WATER;

/** Provider for tags added by mantle, generally not useful for other mods */
@Internal
public class MantleFluidTagProvider extends FabricTagProvider.FluidTagProvider {
  public MantleFluidTagProvider(FabricDataOutput output, CompletableFuture<Provider> holders) {
    super(output, holders);
  }

  @Override
  protected void addTags(Provider pProvider) {
    this.getOrCreateTagBuilder(WATER).add(Fluids.WATER, Fluids.FLOWING_WATER);
    this.getOrCreateTagBuilder(LAVA).add(Fluids.LAVA, Fluids.FLOWING_LAVA);
    this.getOrCreateTagBuilder(SOUP)
      .addOptionalTag(BEETROOT_SOUP.location())
      .addOptionalTag(MUSHROOM_STEW.location())
      .addOptionalTag(RABBIT_STEW.location());
  }

  @Override
  public String getName() {
    return "Mantle Fluid Tag Provider";
  }
}
