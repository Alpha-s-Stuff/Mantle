package slimeknights.mantle.lib.util;

import slimeknights.mantle.lib.mixin.accessor.ItemInHandRendererAccessor;

import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.world.item.ItemStack;

public class FirstPersonRendererHelper {
	public static ItemStack getStackInMainHand(ItemInHandRenderer renderer) {
		return ((ItemInHandRendererAccessor) renderer).mantle$getMainHandItem();
	}

	public static ItemStack getStackInOffHand(ItemInHandRenderer renderer) {
		return ((ItemInHandRendererAccessor) renderer).mantle$getOffHandItem();
	}
}
