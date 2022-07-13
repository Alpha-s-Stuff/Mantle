package slimeknights.mantle.transfer.fluid;

import slimeknights.mantle.transfer.fluid.IFluidHandler;
import net.minecraft.world.item.ItemStack;

public interface IFluidHandlerItem extends IFluidHandler {
	ItemStack getContainer();
}
