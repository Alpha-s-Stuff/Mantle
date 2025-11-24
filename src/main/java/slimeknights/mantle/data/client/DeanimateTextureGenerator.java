package slimeknights.mantle.data.client;

import com.mojang.blaze3d.platform.NativeImage;
import io.github.fabricators_of_create.porting_lib.data.ExistingFileHelper;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.data.CachedOutput;
import net.minecraft.resources.ResourceLocation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/** Copies the first frame of the passed texture into its own texture */
public class DeanimateTextureGenerator extends GenericTextureGenerator {
  private static final Map<ResourceLocation,ResourceLocation> deanimate = new HashMap<>();
  private final String folder;
  public DeanimateTextureGenerator(FabricDataOutput packOutput, ExistingFileHelper existingFileHelper, String folder) {
    super(packOutput, existingFileHelper, folder);
    this.folder = folder;
  }

  public DeanimateTextureGenerator(FabricDataOutput packOutput, ExistingFileHelper existingFileHelper) {
    this(packOutput, existingFileHelper, "textures");
  }

  /** Requests the given texture to be deanimated */
  public void deanimate(ResourceLocation source, ResourceLocation destination) {
    ResourceLocation existing = deanimate.putIfAbsent(destination, source);
    if (existing != null && !existing.equals(source)) {
      throw new IllegalArgumentException("Multiple textures are deanimating with the same destination: original - " + existing + ", new - " + source);
    }
  }

  /** Method to override when using directly */
  protected void addTextures() {}

  @Override
  public final CompletableFuture<?> run(CachedOutput cached) {
    List<NativeImage> openedImages = new ArrayList<>();
    addTextures();
    assert existingFileHelper != null;
    return allOf(deanimate.entrySet().stream().map(entry -> {
      try (NativeImage image = read(existingFileHelper, folder, entry.getValue())) {
        // use the width to guess the height
        NativeImage copy = new NativeImage(image.getWidth(), image.getWidth(), true);
        copy.copyFrom(image);
        // can't close the image until it saved, which is a completable future
        openedImages.add(copy);
        return saveImage(cached, entry.getKey(), copy);
      } catch (IOException e) {
        return CompletableFuture.failedFuture(e);
      }
    })).thenRunAsync(() -> {
      for (NativeImage image : openedImages) {
        image.close();
      }
    });
  }

  @Override
  public String getName() {
    return "Texture Deanimator";
  }
}
