package slimeknights.mantle.loot;

import com.google.gson.JsonDeserializer;
import com.mojang.serialization.MapCodec;
import io.github.fabricators_of_create.porting_lib.loot.IGlobalLootModifier;
import io.github.fabricators_of_create.porting_lib.loot.PortingLibLoot;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
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

import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MantleLoot {
  /** Condition to match a block tag and property predicate */
  public static LootItemConditionType BLOCK_TAG_CONDITION;
  /** Function to add block entity texture to a dropped item */
  public static LootItemFunctionType RETEXTURED_FUNCTION;
  /** Function to add a fluid to an item fluid capability */
  public static LootItemFunctionType SET_FLUID_FUNCTION;

  /**
   * Called during serializer registration to register any relevant loot logic
   */
  public static void registerGlobalLootModifiers() {
    RegistryAdapter<MapCodec<? extends IGlobalLootModifier>> adapter = new RegistryAdapter<>(Objects.requireNonNull(PortingLibLoot.GLOBAL_LOOT_MODIFIER_SERIALIZERS), Mantle.modId);
    adapter.register(AddEntryLootModifier.CODEC, "add_entry");
    adapter.register(ReplaceItemLootModifier.CODEC, "replace_item");

    // functions
    RETEXTURED_FUNCTION = registerFunction("fill_retextured_block", RetexturedLootFunction.CODEC);
    SET_FLUID_FUNCTION = registerFunction("set_fluid", SetFluidLootFunction.CODEC);

    // conditions
    BLOCK_TAG_CONDITION = registerCondition("block_tag", BlockTagLootCondition.CODEC);

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
  private static LootItemFunctionType registerFunction(String name, MapCodec<? extends LootItemFunction> serializer) {
    return Registry.register(BuiltInRegistries.LOOT_FUNCTION_TYPE, Mantle.getResource(name), new LootItemFunctionType(serializer));
  }

  /**
   * Registers a loot function
   * @param name        Loot function name
   * @param serializer  Loot function serializer
   * @return  Registered loot function
   */
  private static LootItemConditionType registerCondition(String name, MapCodec<? extends LootItemCondition> serializer) {
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
