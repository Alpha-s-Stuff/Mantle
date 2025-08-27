package slimeknights.mantle.inventory;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/** Forge still uses dumb vanilla logic for determining slot limits instead of their own method */
public class SmartItemHandlerSlot extends SlotItemHandler {
	public SmartItemHandlerSlot(SlottedStorage<ItemVariant> itemHandler, int index, int xPosition, int yPosition) {
		super(itemHandler, index, xPosition, yPosition);
	}

	@Override
	public boolean mayPickup(Player playerIn) {
		return getItem().isEmpty() || super.mayPickup(playerIn);
	}

	@Override
	public int getMaxStackSize(ItemStack stack) {
    var storage = getItemHandler();
		return (int) Math.min(stack.getMaxStackSize(), storage instanceof SlottedStackStorage slottedStackStorage ? slottedStackStorage.getSlotLimit(getSlotIndex()): storage.getSlot(getSlotIndex()).getCapacity());
	}
}
