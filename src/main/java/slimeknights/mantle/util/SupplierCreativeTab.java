package slimeknights.mantle.util;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import java.util.function.Supplier;

/**
 * Item group that sets its item based on an item supplier
 */
public class SupplierCreativeTab {
  /**
   * Creates a new item group
   * @param modId     Tab owner mod ID
   * @param name      Tab name
   * @param supplier  Item stack supplier
   */
  public static CreativeModeTab.Builder create(String modId, String name, Supplier<ItemStack> supplier) {
    return FabricItemGroup.builder(new ResourceLocation(modId, name)).title(Component.translatable(String.format("%s/%s", modId, name))).icon(supplier);
  }

}
