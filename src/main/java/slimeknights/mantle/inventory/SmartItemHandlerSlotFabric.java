package slimeknights.mantle.inventory;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.world.item.ItemStack;
import slimeknights.mantle.transfer.item.SlotItemHandlerFabric;

/** Forge still uses dumb vanilla logic for determining slot limits instead of their own method */
public class SmartItemHandlerSlotFabric extends SlotItemHandlerFabric {
	public SmartItemHandlerSlotFabric(Storage<ItemVariant> itemHandler, int index, int xPosition, int yPosition) {
		super(itemHandler, index, xPosition, yPosition);
	}

	@Override
	public int getMaxStackSize(ItemStack stack) {
		return 64;
	}
}
