package slimeknights.mantle.lib.transfer.item;

import net.minecraft.core.Direction;
import slimeknights.mantle.lib.util.LazyOptional;

import javax.annotation.Nullable;

public interface ItemTransferable {
	@Nullable
	LazyOptional<IItemHandler> getItemHandler(@Nullable Direction direction);
}
