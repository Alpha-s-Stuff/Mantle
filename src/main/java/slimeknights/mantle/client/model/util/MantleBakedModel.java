package slimeknights.mantle.client.model.util;

import com.google.common.collect.Maps;
import lombok.RequiredArgsConstructor;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.mesh.MeshBuilder;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.model.ModelHelper;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.SimpleBakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Vanilla copy of {@link SimpleBakedModel} modified to work with fabric's rendering api.
 */
@RequiredArgsConstructor
public class MantleBakedModel implements BakedModel {
  protected final Mesh mesh;
  protected final boolean hasAmbientOcclusion;
  protected final boolean isGui3d;
  protected final boolean usesBlockLight;
  protected final TextureAtlasSprite particleIcon;
  protected final ItemTransforms transforms;
  protected final ItemOverrides overrides;

  protected List<BakedQuad> unculledFaces;
  protected Map<Direction, List<BakedQuad>> culledFaces;

  @Override
  public void emitBlockQuads(BlockAndTintGetter blockView, BlockState state, BlockPos pos, Supplier<RandomSource> randomSupplier, RenderContext context) {
    mesh.outputTo(context.getEmitter());
  }

  @Override
  public void emitItemQuads(ItemStack stack, Supplier<RandomSource> randomSupplier, RenderContext context) {
    mesh.outputTo(context.getEmitter());
  }

  @Override
  public boolean isVanillaAdapter() {
    return false;
  }

  @Override
  public List<BakedQuad> getQuads(@Nullable BlockState blockState, @Nullable Direction direction, RandomSource randomSource) {
    if (unculledFaces == null || culledFaces == null) {
      List<BakedQuad>[] quads = ModelHelper.toQuadLists(mesh);
      unculledFaces = quads[ModelHelper.NULL_FACE_ID];
      culledFaces = Maps.newEnumMap(Direction.class);
      for (Direction face : Direction.values()) {
        culledFaces.put(face, quads[ModelHelper.toFaceIndex(face)]);
      }
    }
    return direction == null ? this.unculledFaces : this.culledFaces.get(direction);
  }

  public boolean useAmbientOcclusion() {
    return this.hasAmbientOcclusion;
  }

  public boolean isGui3d() {
    return this.isGui3d;
  }

  public boolean usesBlockLight() {
    return this.usesBlockLight;
  }

  public boolean isCustomRenderer() {
    return false;
  }

  public TextureAtlasSprite getParticleIcon() {
    return this.particleIcon;
  }

  public ItemTransforms getTransforms() {
    return this.transforms;
  }

  public ItemOverrides getOverrides() {
    return this.overrides;
  }

  public static class Builder {
    private final MeshBuilder meshBuilder;
    private final ItemOverrides overrides;
    private final boolean hasAmbientOcclusion;
    private TextureAtlasSprite particleIcon;
    private final boolean usesBlockLight;
    private final boolean isGui3d;
    private final ItemTransforms transforms;

    public Builder(boolean pHasAmbientOcclusion, boolean pUsesBlockLight, boolean pIsGui3d, ItemTransforms pTransforms, ItemOverrides pOverrides) {
      this.meshBuilder = RendererAccess.INSTANCE.getRenderer().meshBuilder();

      this.overrides = pOverrides;
      this.hasAmbientOcclusion = pHasAmbientOcclusion;
      this.usesBlockLight = pUsesBlockLight;
      this.isGui3d = pIsGui3d;
      this.transforms = pTransforms;
    }

    public QuadEmitter getEmitter() {
      return meshBuilder.getEmitter();
    }

    public Builder particle(TextureAtlasSprite pParticleIcon) {
      this.particleIcon = pParticleIcon;
      return this;
    }

    public BakedModel build() {
      if (this.particleIcon == null) {
        throw new RuntimeException("Missing particle!");
      } else {
        return new MantleBakedModel(this.meshBuilder.build(), this.hasAmbientOcclusion, this.usesBlockLight, this.isGui3d, this.particleIcon, this.transforms, this.overrides);
      }
    }
  }
}
