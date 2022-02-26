package slimeknights.mantle.lib.extensions;

import slimeknights.mantle.lib.mixin.accessor.BlockEntityAccessor;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;

public interface BlockEntityExtensions {
  // overridden by BlockEntityMixin
	default CompoundTag mantle$getExtraCustomData() {
		return null;
	}

	void mantle$deserializeNBT(BlockState state, CompoundTag nbt);

	default CompoundTag mantle$save(CompoundTag tag) {
		((BlockEntityAccessor) this).mantle$saveMetadata(tag);
		return tag;
	}
}
