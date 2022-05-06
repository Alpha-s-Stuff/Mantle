package slimeknights.mantle.inventory;

import io.github.fabricators_of_create.porting_lib.extensions.SlotExtensions;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.world.item.ItemStack;
import io.github.fabricators_of_create.porting_lib.transfer.item.SlotItemHandler;

/** Forge still uses dumb vanilla logic for determining slot limits instead of their own method */
@SuppressWarnings("UnstableApiUsage")
public class SmartItemHandlerSlot extends SlotItemHandler {
	public SmartItemHandlerSlot(Storage<ItemVariant> itemHandler, int index, int xPosition, int yPosition) {
		super(itemHandler, index, xPosition, yPosition);
	}

	@Override
	public int getMaxStackSize(ItemStack stack) {
		return getItemHandler().getSlotLimit(((SlotExtensions)this).getSlotIndex());
	}
}
