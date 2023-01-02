package slimeknights.mantle.client.model.util;

import com.mojang.math.Transformation;
import io.github.fabricators_of_create.porting_lib.model.geometry.IGeometryBakingContext;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;

/**
 * Wrapper around a {@link IGeometryBakingContext} instance to allow easier extending, mostly for dynamic textures
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
  public boolean isGui3d() {
    return base.isGui3d();
  }

  @Override
  public boolean useBlockLight() {
    return base.useBlockLight();
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
  public ResourceLocation getRenderTypeHint() {
    return base.getRenderTypeHint();
  }

  @Override
  public boolean isComponentVisible(String part, boolean fallback) {
    return base.isComponentVisible(part, fallback);
  }
}
