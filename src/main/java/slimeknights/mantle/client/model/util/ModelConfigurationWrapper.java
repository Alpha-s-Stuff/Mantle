package slimeknights.mantle.client.model.util;

import com.mojang.math.Transformation;
import io.github.fabricators_of_create.porting_lib.model.BlockGeometryBakingContext;
import io.github.fabricators_of_create.porting_lib.model.IGeometryBakingContext;
import io.github.fabricators_of_create.porting_lib.model.IUnbakedGeometry;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverride;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import io.github.fabricators_of_create.porting_lib.model.IModelGeometryPart;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

/**
 * Wrapper around a {@link BlockModel} instance to allow easier extending, mostly for dynamic textures
 */
@SuppressWarnings("WeakerAccess")
public class ModelConfigurationWrapper implements IGeometryBakingContext {
  private final IGeometryBakingContext base;

  /**
   * Creates a new configuration wrapper
   * @param base  Base model configuration
   */
  public ModelConfigurationWrapper(IGeometryBakingContext base) {
    this.base = base;
  }

  @Override
  public String getModelName() {
    return base.getModelName();
  }

  @Override
  public boolean hasMaterial(String name) {
    return base.hasMaterial(name);
  }

  @Override
  public Material getMaterial(String name) {
    return base.getMaterial(name);
  }

  @Override
  public boolean useBlockLight() {
    return base.useBlockLight();
  }

  @Override
  public boolean isGui3d() {
    return base.isGui3d();
  }

  @Override
  public boolean useAmbientOcclusion() {
    return base.useAmbientOcclusion();
  }

  @Override
  public ItemTransforms getTransforms() {
    return base.getTransforms();
  }

  @Override
  public Transformation getRootTransform() {
    return base.getRootTransform();
  }

  @Override
  public @Nullable ResourceLocation getRenderTypeHint() {
    return base.getRenderTypeHint();
  }

  @Override
  public boolean isComponentVisible(String component, boolean fallback) {
    return base.isComponentVisible(component, fallback);
  }
}
