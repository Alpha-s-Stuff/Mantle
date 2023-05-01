package slimeknights.mantle.block.entity;

import io.github.fabricators_of_create.porting_lib.model.data.ModelData;
import io.github.fabricators_of_create.porting_lib.util.Lazy;
import net.fabricmc.fabric.api.rendering.data.v1.RenderAttachmentBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import slimeknights.mantle.block.RetexturedBlock;
import slimeknights.mantle.util.RetexturedHelper;

import javax.annotation.Nonnull;

import static slimeknights.mantle.util.RetexturedHelper.TAG_TEXTURE;

/**
 *  Minimal implementation of retextured blocks by storing data in the block entity. Does not handle syncing the best
 * @deprecated use {@link DefaultRetexturedBlockEntity}
 */
@Deprecated
public class RetexturedBlockEntity extends MantleBlockEntity implements IRetexturedBlockEntity {
  /** Lazy value of model data as it will not change after first fetch */
  private Lazy<ModelData> data = Lazy.of(this::getRetexturedModelData);
  public RetexturedBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
    super(type, pos, state);
  }

  @Nonnull
  @Override
  public ModelData getRenderAttachmentData() {
    return data.get();
  }

  @Override
  protected boolean shouldSyncOnUpdate() {
    return true;
  }

  @Override
  protected void saveSynced(CompoundTag nbt) {
    super.saveSynced(nbt);
    // ensure the texture syncs, by default forge data does not
    if (!nbt.contains("ForgeData")) {
      CompoundTag forgeData = new CompoundTag();
      forgeData.putString(TAG_TEXTURE, getTextureName());
      nbt.put("ForgeData", forgeData);
    }
  }

  @Override
  public void load(CompoundTag tags) {
    String oldName = getTextureName();
    super.load(tags);
    String newName = getTextureName();
    // if the texture name changed, mark the position for rerender
    if (!oldName.equals(newName) && level != null && level.isClientSide) {
      data = () -> data.get().derive().with(RetexturedHelper.BLOCK_PROPERTY, getTexture()).build();
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
