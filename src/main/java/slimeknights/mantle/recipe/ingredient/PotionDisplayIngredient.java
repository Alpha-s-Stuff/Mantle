package slimeknights.mantle.recipe.ingredient;

import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredientSerializer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import slimeknights.mantle.Mantle;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.recipe.helper.LoadableIngredientSerializer;

import javax.annotation.Nullable;
import java.util.List;

/** Ingredient that shows all potion variants on the displayed item list */
public class PotionDisplayIngredient extends ItemIngredient {
  public static final ResourceLocation ID = Mantle.getResource("potion_display");
  /** Ingredient serializer instance */
  public static final LoadableIngredientSerializer<PotionDisplayIngredient> SERIALIZER = new LoadableIngredientSerializer<>(ID, RecordLoadable.create(ItemsField.INSTANCE, TAG_FIELD, PotionDisplayIngredient::new));

  /** last return of {@link Ingredient#getItems()} */
  private List<ItemStack> lastParentStacks = null;
  /** cache for {@link #getMatchingStacks()} */
  private List<ItemStack> displayStacks = null;

  protected PotionDisplayIngredient(List<Item> items, @Nullable TagKey<Item> tag) {
    super(items, tag);
  }

  /** Creates a ingredient matching a list of items */
  public static PotionDisplayIngredient of(List<ItemLike> items) {
    return new PotionDisplayIngredient(toItem(items), null);
  }

  /** Creates a ingredient matching a list of items */
  public static PotionDisplayIngredient of(ItemLike... items) {
    return of(List.of(items));
  }

  /** Creates a ingredient matching a tag */
  public static PotionDisplayIngredient of(TagKey<Item> tag) {
    return new PotionDisplayIngredient(List.of(), tag);
  }

  @Override
  public boolean isSimple() {
    return true;
  }

  @Override
  public List<ItemStack> getMatchingStacks() {
    // if empty, means we want wildcard, show all potions on the stack
    List<ItemStack> parentStacks = super.getMatchingStacks();
    if (lastParentStacks != parentStacks) {
      lastParentStacks = parentStacks;
      displayStacks = BuiltInRegistries.POTION.stream()
        .filter(pot -> pot != Potions.EMPTY)
        .flatMap(pot -> parentStacks.stream().map(item -> PotionUtils.setPotion(item.copy(), pot)))
        .toList();
    }
    return displayStacks;
  }

  @Override
  public CustomIngredientSerializer<? extends AbstractIngredient> getSerializer() {
    return SERIALIZER;
  }
}
