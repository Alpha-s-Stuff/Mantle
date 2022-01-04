package slimeknights.mantle.lib.item;

import net.minecraft.world.item.ItemStack;

public interface CustomMaxCountItem {
	int getItemStackLimit(ItemStack stack);
}
