package slimeknights.mantle.lib.extensions;

import net.minecraft.world.item.crafting.Ingredient;
import slimeknights.mantle.lib.crafting.IIngredientSerializer;

public interface IngredientEx {
  default IIngredientSerializer<? extends Ingredient> getSerializer() {
    return null;
  }

  default boolean isSimple() {
    return true;
  }

  default void invalidate() {}
}
