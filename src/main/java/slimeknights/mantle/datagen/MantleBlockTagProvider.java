package slimeknights.mantle.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup.Provider;
import org.jetbrains.annotations.ApiStatus.Internal;

import java.util.concurrent.CompletableFuture;

import static slimeknights.mantle.datagen.MantleTags.Blocks.ATTACHED_GAUGES;
import static slimeknights.mantle.datagen.MantleTags.Blocks.GAUGES;
import static slimeknights.mantle.datagen.MantleTags.Blocks.GAUGE_TANKS;

/** Provider for tags added by mantle, generally not useful for other mods */
@Internal
public class MantleBlockTagProvider extends FabricTagProvider.BlockTagProvider {
  public MantleBlockTagProvider(FabricDataOutput output, CompletableFuture<Provider> holders) {
    super(output, holders);
  }

  @Override
  protected void addTags(Provider pProvider) {
    this.tag(GAUGES).addOptionalTag(ATTACHED_GAUGES.location()).addOptionalTag(GAUGE_TANKS.location());
  }

  @Override
  public String getName() {
    return "Mantle Block Tag Provider";
  }
}
