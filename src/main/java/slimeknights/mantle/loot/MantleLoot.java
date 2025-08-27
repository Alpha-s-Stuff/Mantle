package slimeknights.mantle.loot;

import com.google.gson.JsonDeserializer;
import com.mojang.serialization.Codec;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.Serializer;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryType;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import slimeknights.mantle.Mantle;
import slimeknights.mantle.loot.condition.BlockTagLootCondition;
import slimeknights.mantle.loot.condition.ContainsItemModifierLootCondition;
import slimeknights.mantle.loot.condition.EmptyModifierLootCondition;
import slimeknights.mantle.loot.condition.ILootModifierCondition;
import slimeknights.mantle.loot.condition.InvertedModifierLootCondition;
import slimeknights.mantle.loot.entry.TagPreferenceLootEntry;
import slimeknights.mantle.loot.function.RetexturedLootFunction;
import slimeknights.mantle.loot.function.SetFluidLootFunction;
import slimeknights.mantle.recipe.condition.TagEmptyCondition;
import slimeknights.mantle.recipe.condition.TagFilledCondition;
import slimeknights.mantle.registration.adapter.RegistryAdapter;

import java.util.Objects;

import static slimeknights.mantle.loot.condition.ILootModifierCondition.MODIFIER_CONDITIONS;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MantleLoot {
  /** Matches if the passed tag is empty */
  public static LootItemConditionType TAG_EMPTY;
  /** Matches if the passed tag is filled */
  public static LootItemConditionType TAG_FILLED;
  /** Condition to match a block tag and property predicate */
  public static LootItemConditionType BLOCK_TAG_CONDITION;
  /** Function to add block entity texture to a dropped item */
  public static LootItemFunctionType RETEXTURED_FUNCTION;
  /** Function to add a fluid to an item fluid capability */
  public static LootItemFunctionType SET_FLUID_FUNCTION;
  /** Entry to pull a value from a tag preference */
  public static LootPoolEntryType TAG_PREFERENCE;


  /**
   * Called during serializer registration to register any relevant loot logic
   */
  public static void registerGlobalLootModifiers(final RegisterEvent event) {
    ResourceKey<?> key = event.getRegistryKey();

    if (key == ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS) {
      RegistryAdapter<Codec<? extends IGlobalLootModifier>> adapter = new RegistryAdapter<>(Objects.requireNonNull(event.getForgeRegistry()));
      adapter.register(AddEntryLootModifier.CODEC, "add_entry");
      adapter.register(ReplaceItemLootModifier.CODEC, "replace_item");

      // loot modifier conditions
      MODIFIER_CONDITIONS.registerDeserializer(InvertedModifierLootCondition.ID, (JsonDeserializer<? extends ILootModifierCondition>)InvertedModifierLootCondition::deserialize);
      MODIFIER_CONDITIONS.registerDeserializer(EmptyModifierLootCondition.ID, EmptyModifierLootCondition.INSTANCE);
      MODIFIER_CONDITIONS.registerDeserializer(ContainsItemModifierLootCondition.ID, (JsonDeserializer<? extends ILootModifierCondition>)ContainsItemModifierLootCondition::deserialize);
    } else if (key == Registries.LOOT_FUNCTION_TYPE) {
      RETEXTURED_FUNCTION = registerFunction("fill_retextured_block", RetexturedLootFunction.SERIALIZER);
      SET_FLUID_FUNCTION = registerFunction("set_fluid", SetFluidLootFunction.SERIALIZER);

    } else if (key == Registries.LOOT_CONDITION_TYPE) {
      BLOCK_TAG_CONDITION = Registry.register(BuiltInRegistries.LOOT_CONDITION_TYPE, Mantle.getResource("block_tag"), new LootItemConditionType(BlockTagLootCondition.SERIALIZER));
      TAG_EMPTY = Registry.register(BuiltInRegistries.LOOT_CONDITION_TYPE, TagEmptyCondition.SERIALIZER.getID(), new LootItemConditionType(TagEmptyCondition.SERIALIZER));
      TAG_FILLED = Registry.register(BuiltInRegistries.LOOT_CONDITION_TYPE, TagFilledCondition.SERIALIZER.getID(), new LootItemConditionType(TagFilledCondition.SERIALIZER));

    } else if (key == Registries.LOOT_POOL_ENTRY_TYPE) {
      TAG_PREFERENCE = Registry.register(BuiltInRegistries.LOOT_POOL_ENTRY_TYPE, Mantle.getResource("tag_preference"), new LootPoolEntryType(new TagPreferenceLootEntry.Serializer()));
    }
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
}
