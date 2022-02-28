package slimeknights.mantle.lib.block;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;

public interface CustomUpdateTagHandlingBlockEntity {
  default void handleUpdateTag(CompoundTag tag) {
    ((BlockEntity) this).load(tag);
  }
}
