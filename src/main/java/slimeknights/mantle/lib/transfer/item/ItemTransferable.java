package slimeknights.mantle.lib.transfer.item;

import slimeknights.mantle.lib.util.LazyOptional;

import javax.annotation.Nullable;

import net.minecraft.core.Direction;

public interface ItemTransferable {
	@Nullable
	LazyOptional<IItemHandler> getItemHandler(@Nullable Direction direction);
}
