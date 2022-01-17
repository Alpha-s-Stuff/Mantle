package slimeknights.mantle.client.model.util;

import slimeknights.mantle.lib.mixin.accessor.BlockModelAccessor;

import javax.annotation.Nullable;

import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.Material;

/**
 * Wrapper around a {@link IModelConfiguration} instance to allow easier extending, mostly for dynamic textures
 */
@SuppressWarnings("WeakerAccess")
public class ModelConfigurationWrapper extends BlockModel {
  private final BlockModel base;

  /**
   * Creates a new configuration wrapper
   * @param base  Base model configuration
   */
  public ModelConfigurationWrapper(BlockModel base) {
    super(base.parentLocation, base.getElements(), ((BlockModelAccessor)base).getTextureMap(), base.hasAmbientOcclusion(), base.getGuiLight(), base.getTransforms(), base.getOverrides());
    this.base = base;
  }

  @Nullable
  @Override
  public BlockModel getRootModel() {
    return base.getRootModel();
  }

//  @Override
//  public String getModelName() {
//    return base.();
//  }

  @Override
  public boolean hasTexture(String name) {
    return base.hasTexture(name);
  }

  @Override
  public Material getMaterial(String name) {
    return base.getMaterial(name);
  }

  @Override
  public boolean isResolved() {
    return base.isResolved();
  }

//  @Override
//  public boolean isSideLit() {
//    return base.isSideLit();
//  }

//  @Override
//  public boolean useSmoothLighting() {
//    return base.useSmoothLighting();
//  }

  @Override
  public ItemTransforms getTransforms() {
    return base.getTransforms();
  }

//  @Override
//  public ModelState getCombinedTransform() {
//    return base.getCombinedTransform();
//  }

//  @Override
//  public boolean getPartVisibility(IModelGeometryPart part, boolean fallback) {
//    return base.getPartVisibility(part, fallback);
//  }
}
