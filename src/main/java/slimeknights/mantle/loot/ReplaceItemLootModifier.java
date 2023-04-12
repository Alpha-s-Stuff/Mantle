package slimeknights.mantle.loot;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.fabricators_of_create.porting_lib.loot.IGlobalLootModifier;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.util.GsonHelper;
import slimeknights.mantle.data.GlobalLootModifierProvider;
import io.github.fabricators_of_create.porting_lib.loot.LootModifier;
import slimeknights.mantle.transfer.item.ItemHandlerHelper;
import slimeknights.mantle.loot.builder.AbstractLootModifierBuilder;
import slimeknights.mantle.recipe.helper.ItemOutput;
import slimeknights.mantle.recipe.helper.RecipeHelper;
import slimeknights.mantle.util.JsonHelper;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

/** Loot modifier to replace an item with another */
public class ReplaceItemLootModifier extends LootModifier {
  public static final Codec<ReplaceItemLootModifier> CODEC = RecordCodecBuilder.create(inst -> {
    Codec<Ingredient> ingredientCodec = Codec.PASSTHROUGH.flatXmap(dynamic -> {
      JsonElement obj = IGlobalLootModifier.getJson(dynamic).getAsJsonArray().get(0);
      Ingredient original;
      JsonElement element = JsonHelper.getElement(obj.getAsJsonObject(), "original");
      if (element.isJsonPrimitive()) {
        original = Ingredient.of(RecipeHelper.deserializeItem(element.getAsString(), "original", Item.class));
      } else {
        original = Ingredient.fromJson(element);
      }
      return DataResult.success(original);
    }, ingredient -> {
      return DataResult.success(new Dynamic<>(JsonOps.INSTANCE, ingredient.toJson()));
    });
    Codec<ItemOutput> itemOutputCodec = Codec.PASSTHROUGH.flatXmap(dynamic -> {
      JsonElement obj = IGlobalLootModifier.getJson(dynamic);
      return DataResult.success(ItemOutput.fromJson(JsonHelper.getElement(obj.getAsJsonObject(), "replacement")));
    }, itemOutput -> {
      return DataResult.success(new Dynamic<>(JsonOps.INSTANCE, itemOutput.serialize()));
    });

    Codec<LootItemFunction[]> lootItemFunctionCodec = Codec.PASSTHROUGH.flatXmap(dynamic -> {
      LootItemFunction[] functions = AddEntryLootModifier.GSON.fromJson(IGlobalLootModifier.getJson(dynamic), LootItemFunction[].class);
      return DataResult.success(functions);
    }, lootItemFunctions -> {
      if (lootItemFunctions.length > 0) {
        return DataResult.success(new Dynamic<>(JsonOps.INSTANCE, AddEntryLootModifier.GSON.toJsonTree(lootItemFunctions, LootItemFunction[].class)));
      }
      return DataResult.error(() -> "None");
    });
    return codecStart(inst).and(ingredientCodec.fieldOf("original").forGetter(modifier -> modifier.original)).and(itemOutputCodec.fieldOf("replacement").forGetter(modifier -> modifier.replacement)).and(lootItemFunctionCodec.fieldOf("functions").forGetter(modifier -> modifier.functions))
      .apply(inst, ReplaceItemLootModifier::new);
  });

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
    return new ObjectArrayList<>(generatedLoot.stream().map(stack -> {
      if (original.test(stack)) {
        ItemStack replacement = this.replacement.get();
        return combinedFunctions.apply(ItemHandlerHelper.copyStackWithSize(replacement, replacement.getCount() * stack.getCount()), context);
      }
      return stack;
    }).collect(Collectors.toList()));
  }

  @Override
  public Codec<? extends IGlobalLootModifier> codec() {
    return CODEC;
  }

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
      provider.add(name, MantleLoot.REPLACE_ITEM, new ReplaceItemLootModifier(getConditions(), input, replacement, functions.toArray(new LootItemFunction[0])));
    }
  }
}
