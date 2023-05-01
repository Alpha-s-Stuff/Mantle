package slimeknights.mantle.loot;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.fabricators_of_create.porting_lib.loot.GlobalLootModifierProvider;
import io.github.fabricators_of_create.porting_lib.loot.IGlobalLootModifier;
import io.github.fabricators_of_create.porting_lib.loot.LootModifierManager;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.Deserializers;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import io.github.fabricators_of_create.porting_lib.loot.LootModifier;
import slimeknights.mantle.loot.builder.AbstractLootModifierBuilder;
import slimeknights.mantle.loot.condition.ILootModifierCondition;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import static slimeknights.mantle.loot.MantleLoot.LOOT_MOD_GSON;

/** Loot modifier to inject an additional loot entry into an existing table */
public class AddEntryLootModifier extends LootModifier {

  public static final Codec<LootPoolEntryContainer> LOOT_POOL_ENTRY_CODEC = Codec.PASSTHROUGH.flatXmap(
    d -> {
      try {
        LootPoolEntryContainer conditions = LOOT_MOD_GSON.fromJson(MantleLoot.getJson(d), LootPoolEntryContainer.class);
        return DataResult.success(conditions);
      } catch (JsonSyntaxException e) {
        LootModifierManager.LOGGER.warn("Unable to decode loot pool entry", e);
        return DataResult.error(e.getMessage());
      }
    }, entry -> {
      try {
        JsonElement element = LOOT_MOD_GSON.toJsonTree(entry, LootPoolEntryContainer.class);
        return DataResult.success(new Dynamic<>(JsonOps.INSTANCE, element));
      } catch (JsonSyntaxException e) {
        LootModifierManager.LOGGER.warn("Unable to encode loot pool entry", e);
        return DataResult.error(e.getMessage());
      }
    }
  );

  public static final Codec<LootItemFunction[]> LOOT_ITEM_FUNCTIONS_CODEC = Codec.PASSTHROUGH.flatXmap(
    d -> {
      try {
        LootItemFunction[] conditions = LOOT_MOD_GSON.fromJson(MantleLoot.getJson(d), LootItemFunction[].class);
        return DataResult.success(conditions);
      } catch (JsonSyntaxException e) {
        LootModifierManager.LOGGER.warn("Unable to decode loot modifier functions", e);
        return DataResult.error(e.getMessage());
      }
    }, functions -> {
      try {
        JsonElement element = LOOT_MOD_GSON.toJsonTree(functions, LootItemFunction[].class);
        return DataResult.success(new Dynamic<>(JsonOps.INSTANCE, element));
      } catch (JsonSyntaxException e) {
        LootModifierManager.LOGGER.warn("Unable to encode loot modifier functions", e);
        return DataResult.error(e.getMessage());
      }
    }
  );

  public static final Codec<AddEntryLootModifier> CODEC = RecordCodecBuilder.create(
    instance -> LootModifier.codecStart(instance).and(
      instance.group(
        ILootModifierCondition.LOOT_MODIFIER_CONDITIONS_CODEC.optionalFieldOf("modifier_conditions").forGetter(lm ->  Optional.ofNullable(lm.modifierConditions)),
        LOOT_POOL_ENTRY_CODEC.fieldOf("entry").forGetter(lm -> lm.entry),
        LOOT_ITEM_FUNCTIONS_CODEC.optionalFieldOf("functions").forGetter(lm -> Optional.ofNullable(lm.functions))
      )
    ).apply(instance, (conditions, modifiers, entry, functions) -> new AddEntryLootModifier(conditions, modifiers.orElse(new ILootModifierCondition[0]), entry, functions.orElse(new LootItemFunction[0])))
  );

  /** Additional conditions that can consider the previously generated loot */
  private final ILootModifierCondition[] modifierConditions;
  /** Entry for generating loot */
  private final LootPoolEntryContainer entry;
  /** Functions to apply to the entry, allows adding functions to parented loot entries such as alternatives */
  private final LootItemFunction[] functions;
  /** Functions merged into a single function for ease of use */
  private final BiFunction<ItemStack, LootContext, ItemStack> combinedFunctions;

  protected AddEntryLootModifier(LootItemCondition[] conditionsIn, ILootModifierCondition[] modifierConditions, LootPoolEntryContainer entry, LootItemFunction[] functions) {
    super(conditionsIn);
    this.modifierConditions = modifierConditions;
    this.entry = entry;
    this.functions = functions;
    this.combinedFunctions = LootItemFunctions.compose(functions);
  }

  /** Creates a builder for this loot modifier */
  public static Builder builder(LootPoolEntryContainer entry) {
    return new Builder(entry);
  }

  /** Creates a builder for this loot modifier */
  public static Builder builder(LootPoolEntryContainer.Builder<?> builder) {
    return builder(builder.build());
  }

  @Nonnull
  @Override
  protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
    // if any condition fails, exit immediately
    for (ILootModifierCondition modifierCondition : modifierConditions) {
      if (!modifierCondition.test(generatedLoot, context)) {
        return generatedLoot;
      }
    }
    // generate the actual entry
    Consumer<ItemStack> consumer = LootItemFunction.decorate(this.combinedFunctions, generatedLoot::add, context);
    entry.expand(context, generator -> generator.createItemStack(consumer, context));
    return generatedLoot;
  }

  @Override
  public Codec<? extends IGlobalLootModifier> codec() {
    return AddEntryLootModifier.CODEC;
  }

  /*public static class Serializer extends GlobalLootModifierSerializer<AddEntryLootModifier> {

    @Override
    public AddEntryLootModifier read(ResourceLocation location, JsonObject object, LootItemCondition[] conditions) {
      LootPoolEntryContainer entry = GSON.fromJson(GsonHelper.getAsJsonObject(object, "entry"), LootPoolEntryContainer.class);

      // loot modifier conditions
      ILootModifierCondition[] modifierConditions;
      if (object.has("post_conditions")) {
        modifierConditions = GSON.fromJson(GsonHelper.getAsJsonArray(object, "modifier_conditions"), ILootModifierCondition[].class);
      } else {
        modifierConditions = new ILootModifierCondition[0];
      }

      // functions
      LootItemFunction[] functions;
      if (object.has("functions")) {
        functions = GSON.fromJson(GsonHelper.getAsJsonArray(object, "functions"), LootItemFunction[].class);
      } else {
        functions = new LootItemFunction[0];
      }
      return new AddEntryLootModifier(conditions, modifierConditions, entry, functions);
    }

    @Override
    public JsonObject write(AddEntryLootModifier instance) {
      JsonObject object = makeConditions(instance.conditions);
      if (instance.modifierConditions.length > 0) {
        object.add("modifier_conditions", GSON.toJsonTree(instance.modifierConditions, ILootModifierCondition[].class));
      }
      object.add("entry", GSON.toJsonTree(instance.entry, LootPoolEntryContainer.class));
      if (instance.functions.length > 0) {
        object.add("functions", GSON.toJsonTree(instance.functions, LootItemFunction[].class));
      }
      return object;
    }
  }*/

  /** Builder for a conditional loot entry */
  @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
  public static class Builder extends AbstractLootModifierBuilder<Builder> {

    private final List<ILootModifierCondition> modifierConditions = new ArrayList<>();
    private final LootPoolEntryContainer entry;
    private final List<LootItemFunction> functions = new ArrayList<>();

    /**
     * Adds a loot entry condition to the builder
     */
    public Builder addCondition(ILootModifierCondition condition) {
      modifierConditions.add(condition);
      return this;
    }

    /**
     * Adds a loot function to the builder
     */
    public Builder addFunction(LootItemFunction function) {
      functions.add(function);
      return this;
    }

    @Override
    public void build(String name, GlobalLootModifierProvider provider) {
      provider.add(name, new AddEntryLootModifier(getConditions(), modifierConditions.toArray(new ILootModifierCondition[0]), entry, functions.toArray(new LootItemFunction[0])));
    }
  }
}
