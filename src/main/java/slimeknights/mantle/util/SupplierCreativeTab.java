package slimeknights.mantle.util;

import net.fabricmc.fabric.impl.item.group.ItemGroupExtensions;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import java.util.function.Supplier;

/**
 * Item group that sets its item based on an item supplier
 */
public class SupplierCreativeTab extends CreativeModeTab {
  private final Supplier<ItemStack> supplier;

  /**
   * Creates a new item group
   * @param modId     Tab owner mod ID
   * @param name      Tab name
   * @param supplier  Item stack supplier
   */
  public SupplierCreativeTab(String modId, String name, Supplier<ItemStack> supplier) {
    super(createTabIndex(), String.format("%s.%s", modId, name));
    this.setRecipeFolderName(String.format("%s/%s", modId, name));
    this.supplier = supplier;
  }

  @Override
  public ItemStack makeIcon() {
    return supplier.get();
  }

  private static int createTabIndex() {
    ((ItemGroupExtensions)CreativeModeTab.TAB_BUILDING_BLOCKS).fabric_expandArray();
    return CreativeModeTab.TABS.length - 1;
  }

}
