package slimeknights.mantle.loot;

import com.google.gson.JsonDeserializer;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import io.github.fabricators_of_create.porting_lib.loot.IGlobalLootModifier;
import io.github.fabricators_of_create.porting_lib.loot.PortingLibLoot;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.Serializer;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import slimeknights.mantle.Mantle;
import slimeknights.mantle.loot.condition.BlockTagLootCondition;
import slimeknights.mantle.loot.condition.ContainsItemModifierLootCondition;
import slimeknights.mantle.loot.condition.EmptyModifierLootCondition;
import slimeknights.mantle.loot.condition.ILootModifierCondition;
import slimeknights.mantle.loot.condition.InvertedModifierLootCondition;
import slimeknights.mantle.loot.function.RetexturedLootFunction;
import slimeknights.mantle.loot.function.SetFluidLootFunction;
import slimeknights.mantle.registration.adapter.RegistryAdapter;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
//@ObjectHolder(value = Mantle.modId)
public class MantleLoot {
  public static final Codec<LootItemFunction[]> LOOT_ITEM_FUNCTION_CODEC = Codec.PASSTHROUGH.flatXmap(dynamic -> {
    LootItemFunction[] functions = AddEntryLootModifier.GSON.fromJson(IGlobalLootModifier.getJson(dynamic), LootItemFunction[].class);
    return DataResult.success(functions);
  }, lootItemFunctions -> DataResult.success(new Dynamic<>(JsonOps.INSTANCE, AddEntryLootModifier.GSON.toJsonTree(lootItemFunctions, LootItemFunction[].class))));

  /** Condition to match a block tag and property predicate */
  public static LootItemConditionType BLOCK_TAG_CONDITION;
  /** Function to add block entity texture to a dropped item */
  public static LootItemFunctionType RETEXTURED_FUNCTION;
  /** Function to add a fluid to an item fluid capability */
  public static LootItemFunctionType SET_FLUID_FUNCTION;

  /** Loot modifier to get loot from an entry for generated loot */
  public static Codec<AddEntryLootModifier> ADD_ENTRY;
  /** Loot modifier to replace all instances of one item with another */
  public static Codec<ReplaceItemLootModifier> REPLACE_ITEM;

  /**
   * Called during serializer registration to register any relevant loot logic
   */
  public static void registerGlobalLootModifiers() {
    RegistryAdapter<Codec<? extends IGlobalLootModifier>> adapter = new RegistryAdapter<>(PortingLibLoot.GLOBAL_LOOT_MODIFIER_SERIALIZERS.get(), Mantle.modId);
    ADD_ENTRY = adapter.register(AddEntryLootModifier.CODEC, "add_entry");
    REPLACE_ITEM = adapter.register(ReplaceItemLootModifier.CODEC, "replace_item");

    // functions
    RETEXTURED_FUNCTION = registerFunction("fill_retextured_block", RetexturedLootFunction.SERIALIZER);
    SET_FLUID_FUNCTION = registerFunction("set_fluid", SetFluidLootFunction.SERIALIZER);

    // conditions
    BLOCK_TAG_CONDITION = registerCondition("block_tag", BlockTagLootCondition.SERIALIZER);

    // loot modifier conditions
    registerCondition(InvertedModifierLootCondition.ID, InvertedModifierLootCondition::deserialize);
    registerCondition(EmptyModifierLootCondition.ID, EmptyModifierLootCondition.INSTANCE);
    registerCondition(ContainsItemModifierLootCondition.ID, ContainsItemModifierLootCondition::deserialize);
  }

  /**
   * Registers a loot function
   * @param name        Loot function name
   * @param serializer  Loot function serializer
   * @return  Registered loot function
   */
  private static LootItemFunctionType registerFunction(String name, Serializer<? extends LootItemFunction> serializer) {
    return Registry.register(BuiltInRegistries.LOOT_FUNCTION_TYPE, Mantle.getResource(name), new LootItemFunctionType(serializer));
  }

  /**
   * Registers a loot function
   * @param name        Loot function name
   * @param serializer  Loot function serializer
   * @return  Registered loot function
   */
  private static LootItemConditionType registerCondition(String name, Serializer<? extends LootItemCondition> serializer) {
    return Registry.register(BuiltInRegistries.LOOT_CONDITION_TYPE, Mantle.getResource(name), new LootItemConditionType(serializer));
  }

  /**
   * Registers a loot condition
   * @param id            Loot condition id
   * @param deserializer  Loot condition deserializer
   */
  private static void registerCondition(ResourceLocation id, JsonDeserializer<? extends ILootModifierCondition> deserializer) {
    ILootModifierCondition.MODIFIER_CONDITIONS.registerDeserializer(id, deserializer);
  }
}
