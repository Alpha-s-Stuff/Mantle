package slimeknights.mantle.loot.condition;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.fabricators_of_create.porting_lib.loot.LootModifier;
import io.github.fabricators_of_create.porting_lib.loot.LootModifierManager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.Deserializers;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import slimeknights.mantle.data.GenericRegisteredSerializer;
import slimeknights.mantle.data.GenericRegisteredSerializer.IJsonSerializable;

import java.util.List;

import static io.github.fabricators_of_create.porting_lib.loot.IGlobalLootModifier.getJson;
import static slimeknights.mantle.loot.MantleLoot.LOOT_MOD_GSON;

/** Condition for the global loot modifier add entry */
public interface ILootModifierCondition extends IJsonSerializable {
  Codec<ILootModifierCondition[]> LOOT_MODIFIER_CONDITIONS_CODEC = Codec.PASSTHROUGH.flatXmap(
    d -> {
      try {
        ILootModifierCondition[] conditions = LOOT_MOD_GSON.fromJson(getJson(d), ILootModifierCondition[].class);
        return DataResult.success(conditions);
      } catch (JsonSyntaxException e) {
        LootModifierManager.LOGGER.warn("Unable to decode loot modifier conditions", e);
        return DataResult.error(e.getMessage());
      }
    }, conditions -> {
      try {
        JsonElement element = LOOT_MOD_GSON.toJsonTree(conditions, ILootModifierCondition[].class);
        return DataResult.success(new Dynamic<>(JsonOps.INSTANCE, element));
      } catch (JsonSyntaxException e) {
        LootModifierManager.LOGGER.warn("Unable to encode loot modifier conditions", e);
        return DataResult.error(e.getMessage());
      }
    }
  );

  /** Serializer to register conditions with */
  GenericRegisteredSerializer<ILootModifierCondition> MODIFIER_CONDITIONS = new GenericRegisteredSerializer<>();

  /** Checks if this condition passes */
  boolean test(List<ItemStack> generatedLoot, LootContext context);

  /** Creates an inverted instance of this condition */
  default ILootModifierCondition inverted() {
    return new InvertedModifierLootCondition(this);
  }
}
