package slimeknights.mantle.loot;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.fabricators_of_create.porting_lib.loot.GlobalLootModifierProvider;
import io.github.fabricators_of_create.porting_lib.loot.IGlobalLootModifier;
import io.github.fabricators_of_create.porting_lib.loot.LootModifierManager;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import net.minecraft.core.Registry;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.util.GsonHelper;
import net.minecraft.resources.ResourceLocation;
import io.github.fabricators_of_create.porting_lib.loot.LootModifier;
import slimeknights.mantle.loot.condition.ILootModifierCondition;
import slimeknights.mantle.transfer.item.ItemHandlerHelper;
import slimeknights.mantle.loot.builder.AbstractLootModifierBuilder;
import slimeknights.mantle.recipe.helper.ItemOutput;
import slimeknights.mantle.recipe.helper.RecipeHelper;
import slimeknights.mantle.util.JsonHelper;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;

import static slimeknights.mantle.loot.AddEntryLootModifier.LOOT_ITEM_FUNCTIONS_CODEC;
import static slimeknights.mantle.loot.AddEntryLootModifier.LOOT_POOL_ENTRY_CODEC;
import static slimeknights.mantle.loot.MantleLoot.LOOT_MOD_GSON;

/** Loot modifier to replace an item with another */
public class ReplaceItemLootModifier extends LootModifier {
  public static final Codec<ItemOutput> ITEM_OUTPUT_CODEC = Codec.PASSTHROUGH.flatXmap(
    d -> {
      try {
        ItemOutput conditions = LOOT_MOD_GSON.fromJson(MantleLoot.getJson(d), ItemOutput.class);
        return DataResult.success(conditions);
      } catch (JsonSyntaxException e) {
        LootModifierManager.LOGGER.warn("Unable to decode loot modifier conditions", e);
        return DataResult.error(e.getMessage());
      }
    }, conditions -> {
      try {
        JsonElement element = LOOT_MOD_GSON.toJsonTree(conditions, ItemOutput.class);
        return DataResult.success(new Dynamic<>(JsonOps.INSTANCE, element));
      } catch (JsonSyntaxException e) {
        LootModifierManager.LOGGER.warn("Unable to encode loot modifier conditions", e);
        return DataResult.error(e.getMessage());
      }
    }
  );

  public static final Codec<Ingredient> INGREDIENT = Codec.PASSTHROUGH.flatXmap(
    d -> {
      try {
        Ingredient conditions = LOOT_MOD_GSON.fromJson(MantleLoot.getJson(d), Ingredient.class);
        return DataResult.success(conditions);
      } catch (JsonSyntaxException e) {
        LootModifierManager.LOGGER.warn("Unable to decode loot modifier conditions", e);
        return DataResult.error(e.getMessage());
      }
    }, ingredient -> {
      try {
        JsonElement element = LOOT_MOD_GSON.toJsonTree(ingredient, Ingredient.class);
        return DataResult.success(new Dynamic<>(JsonOps.INSTANCE, element));
      } catch (JsonSyntaxException e) {
        LootModifierManager.LOGGER.warn("Unable to encode loot modifier ingredient", e);
        return DataResult.error(e.getMessage());
      }
    }
  );


  public static final Codec<ReplaceItemLootModifier> CODEC = RecordCodecBuilder.create(
    instance -> LootModifier.codecStart(instance).and(
      instance.group(
        INGREDIENT.fieldOf("original").forGetter(lm -> lm.original),
        ITEM_OUTPUT_CODEC.fieldOf("replacement").forGetter(lm -> lm.replacement),
        LOOT_ITEM_FUNCTIONS_CODEC.optionalFieldOf("functions").forGetter(lm -> Optional.ofNullable(lm.functions))
      )
    ).apply(instance, (conditions, original, replacement, functions) -> new ReplaceItemLootModifier(conditions, original, replacement, functions.orElse(new LootItemFunction[0])))
  );

  /** Ingredient to test for the original item */
  private final Ingredient original;
  /** Item for the replacement */
  private final ItemOutput replacement;
  /** Functions to apply to the replacement */
  private final LootItemFunction[] functions;
  /** Functions merged into a single function for ease of use */
  private final BiFunction<ItemStack, LootContext, ItemStack> combinedFunctions;

  protected ReplaceItemLootModifier(LootItemCondition[] conditionsIn, Ingredient original, ItemOutput replacement, LootItemFunction[] functions) {
    super(conditionsIn);
    this.original = original;
    this.replacement = replacement;
    this.functions = functions;
    this.combinedFunctions = LootItemFunctions.compose(functions);
  }

  /** Creates a builder to create a loot modifier */
  public static Builder builder(Ingredient original, ItemOutput replacement) {
    return new Builder(original, replacement);
  }

  @Nonnull
  @Override
  protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
    return generatedLoot.stream().map(stack -> {
      if (original.test(stack)) {
        ItemStack replacement = this.replacement.get();
        return combinedFunctions.apply(ItemHandlerHelper.copyStackWithSize(replacement, replacement.getCount() * stack.getCount()), context);
      }
      return stack;
    }).collect(ObjectArrayList::new, ObjectList::add, ObjectList::addAll);
  }

  @Override
  public Codec<? extends IGlobalLootModifier> codec() {
    return CODEC;
  }

  /*public static class Serializer extends GlobalLootModifierSerializer<ReplaceItemLootModifier> {
    @Override
    public ReplaceItemLootModifier read(ResourceLocation location, JsonObject object, LootItemCondition[] conditions) {
      Ingredient original;
      JsonElement element = JsonHelper.getElement(object, "original");
      if (element.isJsonPrimitive()) {
        original = Ingredient.of(RecipeHelper.deserializeItem(element.getAsString(), "original", Item.class));
      } else {
        original = Ingredient.fromJson(element);
      }
      ItemOutput replacement = ItemOutput.fromJson(JsonHelper.getElement(object, "replacement"));
      // functions
      LootItemFunction[] functions;
      if (object.has("functions")) {
        functions = LOOT_MOD_GSON.fromJson(GsonHelper.getAsJsonArray(object, "functions"), LootItemFunction[].class);
      } else {
        functions = new LootItemFunction[0];
      }
      return new ReplaceItemLootModifier(conditions, original, replacement, functions);
    }

    @Override
    public JsonObject write(ReplaceItemLootModifier instance) {
      JsonObject object = makeConditions(instance.conditions);
      object.add("original", instance.original.toJson());
      object.add("replacement", instance.replacement.serialize());
      if (instance.functions.length > 0) {
        object.add("functions", LOOT_MOD_GSON.toJsonTree(instance.functions, LootItemFunction[].class));
      }
      return object;
    }
  }*/

  /** Logic to build this modifier for datagen */
  @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
  public static class Builder extends AbstractLootModifierBuilder<Builder> {
    private final Ingredient input;
    private final ItemOutput replacement;
    private final List<LootItemFunction> functions = new ArrayList<>();

    /**
     * Adds a loot function to the builder
     */
    public Builder addFunction(LootItemFunction function) {
      functions.add(function);
      return this;
    }

    @Override
    public void build(String name, GlobalLootModifierProvider provider) {
      provider.add(name, new ReplaceItemLootModifier(getConditions(), input, replacement, functions.toArray(new LootItemFunction[0])));
    }
  }
}
