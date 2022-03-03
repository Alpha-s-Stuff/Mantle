package slimeknights.mantle.recipe.ingredient;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntComparators;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import slimeknights.mantle.Mantle;
import io.github.fabricators_of_create.porting_lib.crafting.CraftingHelper;
import io.github.fabricators_of_create.porting_lib.crafting.IIngredientSerializer;
import slimeknights.mantle.util.JsonHelper;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.stream.Stream;

/**
 * Ingredient that matches everything from another ingredient, minus a second ingredient
 */
public class IngredientDifference extends Ingredient {
  public static final ResourceLocation ID = Mantle.getResource("difference");
  public static final IIngredientSerializer<IngredientDifference> SERIALIZER = new Serializer();

  private final Ingredient base;
  private final Ingredient subtracted;
  private ItemStack[] filteredMatchingStacks;
  private IntList packedMatchingStacks;

  protected IngredientDifference(Ingredient base, Ingredient subtracted) {
    super(Stream.empty());
    this.base = base;
    this.subtracted = subtracted;
  }

  /**
   * Gets the set difference from the two ingredients
   * @param base     Base ingredient
   * @param subtracted  Ingredient to subtract
   * @return  Ingredient that {@code base} anything in base that is not in {@code without}
   */
  public static IngredientDifference difference(Ingredient base, Ingredient subtracted) {
    return new IngredientDifference(base, subtracted);
  }

  @Override
  public boolean test(@Nullable ItemStack stack) {
    if (stack == null || stack.isEmpty()) {
      return false;
    }
    return base.test(stack) && !subtracted.test(stack);
  }

  @Override
  public ItemStack[] getItems() {
    if (this.filteredMatchingStacks == null) {
      this.filteredMatchingStacks = Arrays.stream(base.getItems())
                                          .filter(stack -> !subtracted.test(stack))
                                          .toArray(ItemStack[]::new);
    }
    return filteredMatchingStacks;
  }

  @Override
  public boolean isEmpty() {
    return getItems().length == 0;
  }

  @Override
  public boolean isSimple() {
    return base.isSimple() && subtracted.isSimple();
  }

  @Override
  public void invalidate() {
    super.invalidate();
    this.filteredMatchingStacks = null;
    this.packedMatchingStacks = null;
  }

  @Override
  public IntList getStackingIds() {
    if (this.packedMatchingStacks == null) {
      ItemStack[] matchingStacks = getItems();
      this.packedMatchingStacks = new IntArrayList(matchingStacks.length);
      for(ItemStack stack : matchingStacks) {
        this.packedMatchingStacks.add(StackedContents.getStackingIndex(stack));
      }
      this.packedMatchingStacks.sort(IntComparators.NATURAL_COMPARATOR);
    }
    return packedMatchingStacks;
  }

  @Override
  public JsonElement toJson() {
    JsonObject json = new JsonObject();
    json.addProperty("type", ID.toString());
    json.add("base", base.toJson());
    json.add("subtracted", subtracted.toJson());
    return json;
  }

  @Override
  public IIngredientSerializer<IngredientDifference> getSerializer() {
    return SERIALIZER;
  }

  private static class Serializer implements IIngredientSerializer<IngredientDifference> {
    @Override
    public IngredientDifference parse(JsonObject json) {
      Ingredient base = Ingredient.fromJson(JsonHelper.getElement(json, "base"));
      Ingredient without = Ingredient.fromJson(JsonHelper.getElement(json, "subtracted"));
      return new IngredientDifference(base, without);
    }

    @Override
    public IngredientDifference parse(FriendlyByteBuf buffer) {
      Ingredient base = Ingredient.fromNetwork(buffer);
      Ingredient without = Ingredient.fromNetwork(buffer);
      return new IngredientDifference(base, without);
    }

    @Override
    public void write(FriendlyByteBuf buffer, IngredientDifference ingredient) {
      CraftingHelper.write(buffer, ingredient.base);
      CraftingHelper.write(buffer, ingredient.subtracted);
    }
  }
}
