package slimeknights.mantle.recipe.ingredient;

import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredient;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredientSerializer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Ingredient.ItemValue;
import net.minecraft.world.item.crafting.Ingredient.TagValue;
import org.jetbrains.annotations.Nullable;
import slimeknights.mantle.Mantle;
import slimeknights.mantle.data.loadable.Loadable;
import slimeknights.mantle.data.loadable.Loadables;
import slimeknights.mantle.data.loadable.array.ArrayLoadable;
import slimeknights.mantle.data.loadable.field.RecordField;
import slimeknights.mantle.data.loadable.field.UnsyncedField;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.recipe.helper.LoadableIngredientSerializer;
import slimeknights.mantle.util.typed.TypedMap;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

/** Simple ingredient checking for an item with a specific potion */
public class PotionIngredient implements CustomIngredient {
  /** Ingredient serializer instance */
  public static final LoadableIngredientSerializer<PotionIngredient> SERIALIZER = new LoadableIngredientSerializer<>(Mantle.getResource("potion"), RecordLoadable.create(
    ItemsField.INSTANCE,
    new UnsyncedField<>(Loadables.ITEM_TAG.nullableField("tag", i -> i.itemTag)),
    Loadables.POTION.requiredField("potion", i -> i.potion),
    PotionIngredient::new
  ));

  private final Ingredient.Value[] values;
  @Nullable
  private List<ItemStack> itemStacks;
  // item set field for serialization
  private final List<Item> items;
  // item field for serialization
  @Nullable
  private final TagKey<Item> itemTag;
  private final Potion potion;
  protected PotionIngredient(List<Item> items, @Nullable TagKey<Item> itemTag, Potion potion) {
    // potion is added in directly to the parent value stream
    this.values = Stream.concat(
      items.stream().map(item -> new ItemValue(PotionUtils.setPotion(new ItemStack(item), potion))),
      Stream.ofNullable(itemTag).map(tag -> new PotionTagValue(tag, potion))
    ).toArray(Ingredient.Value[]::new);
    this.items = items;
    this.itemTag = null;
    this.potion = potion;
  }

  @Override
  public List<ItemStack> getMatchingStacks() {
    if (this.itemStacks == null) {
      this.itemStacks = Arrays.stream(this.values).flatMap((value) -> value.getItems().stream()).distinct().toList();
    }

    return this.itemStacks;
  }

  /** Creates a potion ingredient matching a list of items */
  public static PotionIngredient of(Potion potion, List<Item> items) {
    return new PotionIngredient(items, null, potion);
  }

  /** Creates a potion ingredient matching a list of items */
  public static PotionIngredient of(Potion potion, Item... items) {
    return of(potion, Arrays.asList(items));
  }

  /** Creates a potion ingredient matching a tag */
  public static PotionIngredient of(Potion potion, TagKey<Item> tag) {
    return new PotionIngredient(List.of(), tag, potion);
  }


  @Override
  public boolean test(@Nullable ItemStack stack) {
    // stack must match, any item must match, and potion must match
    return stack != null && vanillaTest(stack) && PotionUtils.getPotion(stack) == potion;
  }

  protected boolean vanillaTest(ItemStack stack) {
    if (isEmpty()) {
      return stack.isEmpty();
    } else {
      for(ItemStack matchingStack : getMatchingStacks()) {
        if (matchingStack.is(stack.getItem())) {
          return true;
        }
      }

      return false;
    }
  }

  public boolean isEmpty() {
    return this.values.length == 0;
  }

  @Override
  public boolean requiresTesting() {
    return true;
  }

  @Override
  public CustomIngredientSerializer<? extends CustomIngredient> getSerializer() {
    return SERIALIZER;
  }

  /** Custom field that syncs the item tag as items to the client */
  private enum ItemsField implements RecordField<List<Item>,PotionIngredient> {
    INSTANCE;

    private static final Loadable<List<Item>> ITEM_LIST = Loadables.ITEM.list(ArrayLoadable.COMPACT_OR_EMPTY);

    @Override
    public List<Item> get(JsonObject json, TypedMap context) {
      return ITEM_LIST.getOrDefault(json, "item", List.of(), context);
    }

    @Override
    public void serialize(PotionIngredient parent, JsonObject json) {
      if (parent.items.isEmpty()) {
        json.add("item", ITEM_LIST.serialize(parent.items));
      }
    }

    @Override
    public List<Item> decode(FriendlyByteBuf buffer, TypedMap context) {
      return ITEM_LIST.decode(buffer, context);
    }

    @Override
    public void encode(FriendlyByteBuf buffer, PotionIngredient parent) {
      // sync both tag and item values to client
      ITEM_LIST.encode(buffer, parent.getMatchingStacks().stream().map(ItemStack::getItem).toList());
    }
  }

  /** Tag value that sets the potion on each returned item */
  private static class PotionTagValue extends TagValue {
    private final Potion potion;
    public PotionTagValue(TagKey<Item> tag, Potion potion) {
      super(tag);
      this.potion = potion;
    }

    @Override
    public Collection<ItemStack> getItems() {
      return super.getItems().stream()
        .map(item -> PotionUtils.setPotion(item, potion))
        .toList();
    }
  }
}
