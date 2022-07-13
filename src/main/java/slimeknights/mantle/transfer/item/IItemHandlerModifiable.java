package slimeknights.mantle.transfer.item;

import slimeknights.mantle.transfer.item.IItemHandler;
import net.minecraft.world.item.ItemStack;

public interface IItemHandlerModifiable extends IItemHandler {
	void setStackInSlot(int slot, ItemStack stack);
}
