package slimeknights.mantle.inventory;

import io.github.fabricators_of_create.porting_lib.extensions.SlotExtensions;
import io.github.fabricators_of_create.porting_lib.transfer.item.ItemStackHandler;
import io.github.fabricators_of_create.porting_lib.transfer.item.SlotItemHandler;
import net.minecraft.world.item.ItemStack;

/** Forge still uses dumb vanilla logic for determining slot limits instead of their own method */
@SuppressWarnings("UnstableApiUsage")
public class SmartItemHandlerSlot extends SlotItemHandler {
	public SmartItemHandlerSlot(ItemStackHandler itemHandler, int index, int xPosition, int yPosition) {
		super(itemHandler, index, xPosition, yPosition);
	}

	@Override
	public int getMaxStackSize(ItemStack stack) {
		return getItemHandler().getSlotLimit(((SlotExtensions)this).getSlotIndex());
	}
}
