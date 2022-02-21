package slimeknights.mantle.item;

import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.world.item.DoubleHighBlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;

import javax.annotation.Nullable;

public class BurnableTallBlockItem extends DoubleHighBlockItem {

  public BurnableTallBlockItem(Block blockIn, Properties builder, int burnTime) {
    super(blockIn, builder);
    FuelRegistry.INSTANCE.add(this, burnTime);
  }
}
