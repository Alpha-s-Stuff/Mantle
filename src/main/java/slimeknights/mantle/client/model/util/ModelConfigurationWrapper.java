package slimeknights.mantle.client.model.util;

import com.mojang.math.Transformation;
import io.github.fabricators_of_create.porting_lib.models.geometry.IUnbakedGeometry;
import io.github.fabricators_of_create.porting_lib.models.geometry.VisibilityData;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverride;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

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

  @Override
  public List<BlockElement> getElements() {
    return base.getElements();
  }

  @Override
  public boolean isResolved() {
    return base.isResolved();
  }

  @Override
  public List<ItemOverride> getOverrides() {
    return base.getOverrides();
  }

  @Override
  public Collection<ResourceLocation> getDependencies() {
    return base.getDependencies();
  }

  @Override
  public void resolveParents(Function<ResourceLocation, UnbakedModel> function) {
    base.resolveParents(function);
  }

  @Override
  public BakedModel bake(ModelBaker modelBaker, Function<Material, TextureAtlasSprite> function, ModelState modelState, ResourceLocation resourceLocation) {
    return base.bake(modelBaker, function, modelState, resourceLocation);
  }

  @Override
  public BakedModel bake(ModelBaker modelBaker, BlockModel blockModel, Function<Material, TextureAtlasSprite> function, ModelState modelState, ResourceLocation resourceLocation, boolean bl) {
    return base.bake(modelBaker, blockModel, function, modelState, resourceLocation, bl);
  }

  @Override
  public BlockModel getRootModel() {
    return base.getRootModel();
  }

  @Override
  public ItemOverrides getOverrides(ModelBaker pModelBakery, BlockModel pModel, Function<Material, TextureAtlasSprite> textureGetter) {
    return base.getOverrides(pModelBakery, pModel, textureGetter);
  }

  @Override
  public void setCustomGeometry(IUnbakedGeometry<?> geometry) {
    base.setCustomGeometry(geometry);
  }

  @Override
  public IUnbakedGeometry<?> getCustomGeometry() {
    return base.getCustomGeometry();
  }

  @Override
  public VisibilityData getVisibilityData() {
    return base.getVisibilityData();
  }

  @Override
  public void setRootTransform(Transformation rootTransform) {
    base.setRootTransform(rootTransform);
  }
}
