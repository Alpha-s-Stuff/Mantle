package slimeknights.mantle.recipe.ingredient;

import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredient;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public abstract class AbstractIngredient implements CustomIngredient {

  private final Ingredient.Value[] values;
  @Nullable
  private List<ItemStack> itemStacks;

  /**
   * Empty constructor, for the sake of dynamic ingredients
   */
  protected AbstractIngredient() {
    this(Stream.of());
  }

  /**
   * Value constructor, for ingredients that have some vanilla representation
   */
  protected AbstractIngredient(Stream<? extends Ingredient.Value> values) {
    this.values = values.toArray(Ingredient.Value[]::new);
  }

  @Override
  public List<ItemStack> getMatchingStacks() {
    if (this.itemStacks == null) {
      this.itemStacks = Arrays.stream(this.values).flatMap((value) -> value.getItems().stream()).distinct().toList();
    }

    return this.itemStacks;
  }

  @Override
  public boolean requiresTesting() {
    return !isSimple();
  }

  public abstract boolean isSimple();
}
