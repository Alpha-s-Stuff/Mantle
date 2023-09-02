package slimeknights.mantle.client.model.util;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import io.github.fabricators_of_create.porting_lib.models.TransformTypeDependentItemBakedModel;
import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.model.ForwardingBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Supplier;

public class BakedItemModel implements BakedModel, TransformTypeDependentItemBakedModel {
  protected final Mesh quads;
  protected final TextureAtlasSprite particle;
  protected final ItemTransforms transforms;
  protected final ItemOverrides overrides;
  protected final BakedModel guiModel;
  protected final boolean useBlockLight;

  public BakedItemModel(Mesh quads, TextureAtlasSprite particle, ItemTransforms transforms, ItemOverrides overrides, boolean untransformed, boolean useBlockLight)
  {
    this.quads = quads;
    this.particle = particle;
    this.transforms = transforms;
    this.overrides = overrides;
    this.useBlockLight = useBlockLight;
    this.guiModel = untransformed && hasGuiIdentity(transforms) ? new BakedGuiItemModel<>(this) : null;
  }

  private static boolean hasGuiIdentity(ItemTransforms transforms)
  {
    return transforms.getTransform(ItemDisplayContext.GUI) == ItemTransform.NO_TRANSFORM;
  }

  @Override public boolean useAmbientOcclusion() { return true; }
  @Override public boolean isGui3d() { return false; }
  @Override public boolean usesBlockLight() { return useBlockLight; }
  @Override public boolean isCustomRenderer() { return false; }
  @Override public TextureAtlasSprite getParticleIcon() { return particle; }

  @Override
  public ItemTransforms getTransforms() {
    return ItemTransforms.NO_TRANSFORMS;
  }

  @Override public ItemOverrides getOverrides() { return overrides; }

  @Override
  public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand)
  {
    return ImmutableList.of();
  }

  @Override
  public void emitItemQuads(ItemStack stack, Supplier<RandomSource> randomSupplier, RenderContext context) {
    this.quads.outputTo(context.getEmitter());
  }

  @Override
  public BakedModel applyTransform(ItemDisplayContext type, PoseStack poseStack, boolean applyLeftHandTransform, DefaultTransform defaultTransform)
  {
    if (type == ItemDisplayContext.GUI && this.guiModel != null)
    {
      return ((TransformTypeDependentItemBakedModel)this.guiModel).applyTransform(type, poseStack, applyLeftHandTransform, defaultTransform);
    }
    transforms.getTransform(type).apply(applyLeftHandTransform, poseStack);
    return this;
  }

  public static class BakedGuiItemModel<T extends BakedItemModel> extends ForwardingBakedModel implements TransformTypeDependentItemBakedModel
  {

    public BakedGuiItemModel(T originalModel)
    {
      this.wrapped = originalModel;
    }

    @Override
    public List<BakedQuad> getQuads (@Nullable BlockState state, @Nullable Direction side, RandomSource rand)
    {
      return ImmutableList.of();
    }

    @Override
    public void emitItemQuads(ItemStack stack, Supplier<RandomSource> randomSupplier, RenderContext context) {
      context.pushTransform(quad -> quad.lightFace() == Direction.SOUTH);
      super.emitItemQuads(stack, randomSupplier, context);
      context.popTransform();
    }

    @Override
    public BakedModel applyTransform(ItemDisplayContext type, PoseStack poseStack, boolean leftHanded, DefaultTransform defaultTransform)
    {
      if (type == ItemDisplayContext.GUI)
      {
        ((BakedItemModel)wrapped).transforms.getTransform(type).apply(leftHanded, poseStack);
        return this;
      }

      if(this.wrapped instanceof TransformTypeDependentItemBakedModel model)
        return model.applyTransform(type, poseStack, leftHanded, defaultTransform);
      return this;
    }
  }
}
