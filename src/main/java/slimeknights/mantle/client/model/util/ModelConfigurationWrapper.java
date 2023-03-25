package slimeknights.mantle.client.model.util;

import com.mojang.math.Transformation;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;

/**
 * Wrapper around a {@link BlockModel} instance to allow easier extending, mostly for dynamic textures
 */
@SuppressWarnings("WeakerAccess")
public class ModelConfigurationWrapper extends BlockModel {
  private final BlockModel base;

  /**
   * Creates a new configuration wrapper
   * @param base  Base model configuration
   */
  public ModelConfigurationWrapper(BlockModel base) {
    super(base.parentLocation, base.getElements(), base.textureMap, base.hasAmbientOcclusion(), base.getGuiLight(), base.getTransforms(), base.getOverrides());
    this.base = base;
  }

  @Override
  public boolean hasTexture(String name) {
    return base.hasTexture(name);
  }

  @Override
  public Material getMaterial(String name) {
    return base.getMaterial(name);
  }

  @Override
  public GuiLight getGuiLight() {
    return base.getGuiLight();
  }

  @Override
  public boolean hasAmbientOcclusion() {
    return base.hasAmbientOcclusion();
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
  public boolean isComponentVisible(String part, boolean fallback) {
    return base.isComponentVisible(part, fallback);
  }
}
