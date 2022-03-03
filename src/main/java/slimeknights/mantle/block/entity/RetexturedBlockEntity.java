package slimeknights.mantle.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import net.fabricmc.fabric.api.rendering.data.v1.RenderAttachmentBlockEntity;
import slimeknights.mantle.block.RetexturedBlock;
import io.github.fabricators_of_create.porting_lib.model.IModelData;
import io.github.fabricators_of_create.porting_lib.util.Lazy;
import slimeknights.mantle.util.RetexturedHelper;

import javax.annotation.Nonnull;

/**
 * Minimal implementation for {@link IRetexturedBlockEntity}, use alongside {@link RetexturedBlock} and {@link slimeknights.mantle.item.RetexturedBlockItem}
 */
public class RetexturedBlockEntity extends MantleBlockEntity implements IRetexturedBlockEntity, RenderAttachmentBlockEntity {

  /** Lazy value of model data as it will not change after first fetch */
  private final Lazy<IModelData> data = Lazy.of(this::getRetexturedModelData);
  public RetexturedBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
    super(type, pos, state);
  }

  @Nonnull
  @Override
  public Object getRenderAttachmentData() {
    return data.get();
  }

  @Override
  protected boolean shouldSyncOnUpdate() {
    return true;
  }

  @Override
  public void load(CompoundTag tags) {
    String oldName = getTextureName();
    super.load(tags);
    String newName = getTextureName();
    // if the texture name changed, mark the position for rerender
    if (!oldName.equals(newName) && level != null && level.isClientSide) {
      data.get().setData(RetexturedHelper.BLOCK_PROPERTY, getTexture());
//      requestModelDataUpdate();
      BlockState state = getBlockState();
      level.sendBlockUpdated(worldPosition, state, state, 0);
    }
  }

  @Override
  public CompoundTag getTileData() {
    return this.getExtraCustomData();
  }
}
