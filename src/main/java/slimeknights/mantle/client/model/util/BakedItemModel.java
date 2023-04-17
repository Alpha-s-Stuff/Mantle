package slimeknights.mantle.client.model.util;

import javax.annotation.Nullable;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import io.github.fabricators_of_create.porting_lib.models.TransformTypeDependentItemBakedModel;
import net.fabricmc.fabric.api.renderer.v1.model.ForwardingBakedModel;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.state.BlockState;
import com.mojang.math.Transformation;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;

public class BakedItemModel implements BakedModel, TransformTypeDependentItemBakedModel {
  protected final List<BakedQuad> quads;
  protected final TextureAtlasSprite particle;
  protected final ItemTransforms transforms;
  protected final ItemOverrides overrides;
  protected final BakedModel guiModel;
  protected final boolean useBlockLight;

  public BakedItemModel(List<BakedQuad> quads, TextureAtlasSprite particle, ItemTransforms transforms, ItemOverrides overrides, boolean untransformed, boolean useBlockLight)
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
    if (side == null)
    {
      return quads;
    }
    return ImmutableList.of();
  }

  @Override
  public BakedModel applyTransform(ItemDisplayContext type, PoseStack poseStack, boolean applyLeftHandTransform)
  {
    if (type == ItemDisplayContext.GUI && this.guiModel != null)
    {
      return ((TransformTypeDependentItemBakedModel)this.guiModel).applyTransform(type, poseStack, applyLeftHandTransform);
    }
    transforms.getTransform(type).apply(applyLeftHandTransform, poseStack);
    return this;
  }

  public static class BakedGuiItemModel<T extends BakedItemModel> extends ForwardingBakedModel implements TransformTypeDependentItemBakedModel
  {
    private final ImmutableList<BakedQuad> quads;

    public BakedGuiItemModel(T originalModel)
    {
      this.wrapped = originalModel;
      ImmutableList.Builder<BakedQuad> builder = ImmutableList.builder();
      for (BakedQuad quad : originalModel.quads)
      {
        if (quad.getDirection() == Direction.SOUTH)
        {
          builder.add(quad);
        }
      }
      this.quads = builder.build();
    }

    @Override
    public List<BakedQuad> getQuads (@Nullable BlockState state, @Nullable Direction side, RandomSource rand)
    {
      if(side == null)
      {
        return quads;
      }
      return ImmutableList.of();
    }

    @Override
    public BakedModel applyTransform(ItemDisplayContext type, PoseStack poseStack, boolean leftHanded)
    {
      if (type == ItemDisplayContext.GUI)
      {
        ((BakedItemModel)wrapped).transforms.getTransform(type).apply(leftHanded, poseStack);
        return this;
      }

      if(this.wrapped instanceof TransformTypeDependentItemBakedModel model)
        return model.applyTransform(type, poseStack, leftHanded);
      return this;
    }
  }
}
