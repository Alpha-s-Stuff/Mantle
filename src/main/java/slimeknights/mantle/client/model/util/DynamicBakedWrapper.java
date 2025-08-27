package slimeknights.mantle.client.model.util;

import net.fabricmc.fabric.api.renderer.v1.model.ForwardingBakedModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Cross between {@link BakedModelWrapper} and {@link net.minecraftforge.client.model.IDynamicBakedModel}.
 * Used to create a baked model wrapper that has a dynamic {@link #getQuads(BlockState, Direction, RandomSource, ModelData, RenderType)} (BlockState, Direction, Random, IModelData)} without worrying about overriding the deprecated variant.
 * @param <T>  Baked model parent
 */
@SuppressWarnings("WeakerAccess")
public abstract class DynamicBakedWrapper<T extends BakedModel> extends ForwardingBakedModel {

  protected DynamicBakedWrapper(T originalModel) {
    this.wrapped = originalModel;
  }

  @Override
  public boolean isVanillaAdapter() {
    return false;
  }
}
