package slimeknights.mantle.recipe.ingredient;

import com.google.gson.JsonObject;
import net.minecraft.core.Registry;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient.Value;

import java.util.Collection;
import java.util.List;

public class FluidIngredientValue implements Value {
  protected final List<ItemStack> items;
  public FluidIngredientValue(FluidIngredient ingredient) {
    items = Registry.ITEM.stream()
      .map(Item::getDefaultInstance)
      .filter(stack -> FluidContainerIngredient.testStack(stack, ingredient))
      .toList();
  }

  @Override
  public Collection<ItemStack> getItems() {
    return items;
  }

  @Override
  public JsonObject serialize() {
    throw new IllegalStateException("FluidIngredientValue shouldn't be serialized");
  }
}
