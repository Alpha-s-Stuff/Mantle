package slimeknights.mantle.fabric.crafting;

import net.minecraft.world.item.crafting.Ingredient;

public interface IngredientExtensions {
  default IIngredientSerializer<? extends Ingredient> getSerializer() {
    throw new RuntimeException("this should be overridden via mixin. what?");
  }

  default boolean isSimple() {
    throw new RuntimeException("this should be overridden via mixin. what?");
  }

  default void invalidate() {
    throw new RuntimeException("this should be overridden via mixin. what?");
  }
}
