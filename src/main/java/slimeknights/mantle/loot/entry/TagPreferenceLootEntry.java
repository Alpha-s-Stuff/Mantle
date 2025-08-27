package slimeknights.mantle.loot.entry;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryType;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import slimeknights.mantle.loot.MantleLoot;
import slimeknights.mantle.recipe.helper.TagPreference;
import slimeknights.mantle.util.JsonHelper;

import java.util.function.Consumer;

/** Loot entry that returns the preferred item from a tag. See {@link TagPreference} */
public class TagPreferenceLootEntry extends LootPoolSingletonContainer {
  private final TagKey<Item> tag;
  protected TagPreferenceLootEntry(TagKey<Item> tag, int weight, int quality, LootItemCondition[] conditions, LootItemFunction[] functions) {
    super(weight, quality, conditions, functions);
    this.tag = tag;
  }

  @Override
  public LootPoolEntryType getType() {
    return MantleLoot.TAG_PREFERENCE;
  }

  @Override
  protected void createItemStack(Consumer<ItemStack> consumer, LootContext context) {
    TagPreference.getPreference(tag).ifPresent(item -> consumer.accept(new ItemStack(item)));
  }

  /** Creates a new builder */
  @SuppressWarnings("unused") // API
  public static Builder<?> tagPreference(TagKey<Item> tag) {
    return simpleBuilder((weight, quality, conditions, functions) -> new TagPreferenceLootEntry(tag, weight, quality, conditions, functions));
  }

  public static class Serializer extends LootPoolSingletonContainer.Serializer<TagPreferenceLootEntry> {
    @Override
    public void serializeCustom(JsonObject json, TagPreferenceLootEntry object, JsonSerializationContext conditions) {
      super.serializeCustom(json, object, conditions);
      json.addProperty("tag", object.tag.location().toString());
    }

    @Override
    protected TagPreferenceLootEntry deserialize(JsonObject json, JsonDeserializationContext context, int weight, int quality, LootItemCondition[] conditions, LootItemFunction[] functions) {
      TagKey<Item> tag = TagKey.create(Registries.ITEM, JsonHelper.getResourceLocation(json, "tag"));
      return new TagPreferenceLootEntry(tag, weight, quality, conditions, functions);
    }
  }
}
