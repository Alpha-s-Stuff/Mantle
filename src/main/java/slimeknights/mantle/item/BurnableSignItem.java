package slimeknights.mantle.item;

import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SignItem;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;

import javax.annotation.Nullable;

public class BurnableSignItem extends SignItem {

  public BurnableSignItem(Properties propertiesIn, Block floorBlockIn, Block wallBlockIn, int burnTime) {
    super(propertiesIn, floorBlockIn, wallBlockIn);
    FuelRegistry.INSTANCE.add(this, burnTime);
  }
}
