package slimeknights.mantle.block.entity;

import net.fabricmc.fabric.api.blockview.v2.RenderDataBlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;
import slimeknights.mantle.block.RetexturedBlock;
import slimeknights.mantle.client.model.data.IModelData;
import slimeknights.mantle.client.model.data.SinglePropertyData;
import slimeknights.mantle.util.RetexturedHelper;

/**
 * Standard interface that should be used by retexturable tile entities, allows control over where the texture is saved.
 * Note that in the future, more of these methods will be made abstract, discouraging the use of {@link #getTileData()} to store the texture (as we can sync our own tag easier)
 *
 * Use alongside {@link RetexturedBlock} and {@link slimeknights.mantle.item.RetexturedBlockItem}. See {@link DefaultRetexturedBlockEntity} for implementation.
 */
public interface IRetexturedBlockEntity extends RenderDataBlockEntity {
  /* Gets the Forge tile data for the tile entity */
  default CompoundTag getTileData() {
    return ((BlockEntity)this).getCustomData();
  }

  /**
   * Gets the current texture block name. Encouraged to override this to not use {@link #getTileData()}
   * @return Texture block name
   */
  default String getTextureName() {
    return RetexturedHelper.getTextureName(getTileData());
  }

  /**
   * Gets the current texture block
   * @return Texture block
   */
  default Block getTexture() {
    return RetexturedHelper.getBlock(getTextureName());
  }

  /**
   * Updates the texture to the given name. Encouraged to override this to not use {@link #getTileData()}
   * @param name  Texture name
   */
  default void updateTexture(String name) {
    RetexturedHelper.setTexture(getTileData(), name);
  }

  /**
   * Gets the model data instance with the relevant texture block
   * @return  Model data for the TE
   */
  default IModelData getRetexturedModelData() {
    // texture not loaded
    Block block = getTexture();
    // cannot support air, saves a conditional on usage
    if (block == Blocks.AIR) {
      block = null;
    }
    return new SinglePropertyData<>(RetexturedHelper.BLOCK_PROPERTY, block);
  }

  @Override
  @Nullable IModelData getRenderData();
}
