package slimeknights.mantle.item;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;

import net.minecraft.item.Item.Properties;

public class BurnableBlockItem extends BlockItem {
  private final int burnTime;
  public BurnableBlockItem(Block blockIn, Properties builder, int burnTime) {
    super(blockIn, builder);
    this.burnTime = burnTime;
  }

  @Override
  public int getBurnTime(ItemStack itemStack) {
    return burnTime;
  }
}
