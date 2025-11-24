package slimeknights.mantle.fluid.texture;

import io.github.fabricators_of_create.porting_lib.data.ExistingFileHelper;
import io.github.fabricators_of_create.porting_lib.fluids.FluidType;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.resources.ResourceLocation;
import slimeknights.mantle.data.client.DeanimateTextureGenerator;

import java.util.Map.Entry;
import java.util.Set;

/** Generates fluid camera textures using the first frame of the still texture */
public class FluidTextureCameraProvider extends DeanimateTextureGenerator {
  private final AbstractFluidTextureProvider provider;
  /** Fluid types from the provider to ignore */
  private final Set<FluidType> skip;

  public FluidTextureCameraProvider(FabricDataOutput packOutput, ExistingFileHelper existingFileHelper, AbstractFluidTextureProvider provider, Set<FluidType> skip) {
    super(packOutput, existingFileHelper);
    this.provider = provider;
    this.skip = skip;
  }

  public FluidTextureCameraProvider(FabricDataOutput packOutput, ExistingFileHelper existingFileHelper, AbstractFluidTextureProvider provider) {
    this(packOutput, existingFileHelper, provider, Set.of());
  }

  @Override
  protected void addTextures() {
    for (Entry<FluidType, FluidTexture.Builder> entry : provider.getAllTextures().entrySet()) {
      if (!skip.contains(entry.getKey())) {
        FluidTexture.Builder builder = entry.getValue();
        ResourceLocation camera = builder.getCamera();
        if (camera != null) {
          deanimate(builder.getStill(), camera);
        }
      }
    }
  }

  @Override
  public String getName() {
    return "Fluid texture camera provider";
  }
}
