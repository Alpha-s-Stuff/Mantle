package slimeknights.mantle.lib.extensions;

import net.minecraft.world.item.crafting.Ingredient;
import slimeknights.mantle.lib.crafting.IIngredientSerializer;

public interface IngredientEx {
  IIngredientSerializer<? extends Ingredient> getSerializer();

  boolean isSimple();

  default void invalidate() {}
}
