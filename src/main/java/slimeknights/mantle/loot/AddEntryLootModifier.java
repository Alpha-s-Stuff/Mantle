package slimeknights.mantle.loot;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.fabricators_of_create.porting_lib.loot.IGlobalLootModifier;
import io.github.fabricators_of_create.porting_lib.loot.LootModifier;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.Deserializers;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import slimeknights.mantle.data.GlobalLootModifierProvider;
import slimeknights.mantle.loot.builder.AbstractLootModifierBuilder;
import slimeknights.mantle.loot.condition.ILootModifierCondition;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;

/** Loot modifier to inject an additional loot entry into an existing table */
public class AddEntryLootModifier extends LootModifier {
	static final Gson GSON = Deserializers.createFunctionSerializer().registerTypeHierarchyAdapter(ILootModifierCondition.class, ILootModifierCondition.MODIFIER_CONDITIONS).create();

  public static final Codec<AddEntryLootModifier> CODEC = RecordCodecBuilder.create(inst -> {
    Codec<ILootModifierCondition[]> modifierConditionsCodec = Codec.PASSTHROUGH.flatXmap(dynamic -> {
      JsonObject object = IGlobalLootModifier.getJson(dynamic).getAsJsonArray().get(0).getAsJsonObject();
      ILootModifierCondition[] modifierConditions;
      if (object.has("post_conditions")) {
        modifierConditions = GSON.fromJson(GsonHelper.getAsJsonArray(object, "modifier_conditions"), ILootModifierCondition[].class);
      } else {
        modifierConditions = new ILootModifierCondition[0];
      }
      return DataResult.success(modifierConditions);
    }, modifierConditions -> DataResult.success(new Dynamic<>(JsonOps.INSTANCE, GSON.toJsonTree(modifierConditions, ILootModifierCondition[].class))));
    Codec<LootPoolEntryContainer> entryCodec = Codec.PASSTHROUGH.flatXmap(dynamic -> {
      JsonObject object = IGlobalLootModifier.getJson(dynamic).getAsJsonObject();

      return DataResult.success(GSON.fromJson(object, LootPoolEntryContainer.class));
    }, entry -> DataResult.success(new Dynamic<>(JsonOps.INSTANCE, GSON.toJsonTree(entry, LootPoolEntryContainer.class))));
    return codecStart(inst).and(modifierConditionsCodec.fieldOf("modifier_conditions").forGetter(o -> o.modifierConditions)).and(entryCodec.fieldOf("entry").forGetter(o -> o.entry)).and(MantleLoot.LOOT_ITEM_FUNCTION_CODEC.fieldOf("functions").forGetter(o -> o.functions))
      .apply(inst, AddEntryLootModifier::new);
  });

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
  public Codec<AddEntryLootModifier> codec() {
    return CODEC;
  }

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
      provider.add(name, MantleLoot.ADD_ENTRY, new AddEntryLootModifier(getConditions(), modifierConditions.toArray(new ILootModifierCondition[0]), entry, functions.toArray(new LootItemFunction[0])));
    }
  }
}
