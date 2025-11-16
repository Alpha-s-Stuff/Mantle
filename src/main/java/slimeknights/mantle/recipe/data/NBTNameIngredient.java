package slimeknights.mantle.recipe.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import net.fabricmc.fabric.impl.recipe.ingredient.CustomIngredientImpl;
import net.fabricmc.fabric.impl.recipe.ingredient.builtin.NbtIngredient;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;

import javax.annotation.Nullable;
import java.util.stream.Stream;

/**
 * Ingredient for a NBT sensitive item from another mod, should never be used outside datagen
 */
public class NBTNameIngredient extends Ingredient {
  private final ResourceLocation name;
  @Nullable
  private final CompoundTag nbt;

  protected NBTNameIngredient(ResourceLocation name, @Nullable CompoundTag nbt) {
    super(Stream.of(new ItemValue(ItemStack.EMPTY)));
    this.name = name;
    this.nbt = nbt;
  }

  /**
   * Creates an ingredient for the given name and NBT
   * @param name  Item name
   * @param nbt   NBT
   * @return  Ingredient
   */
  public static NBTNameIngredient from(ResourceLocation name, CompoundTag nbt) {
    return new NBTNameIngredient(name, nbt);
  }

  /**
   * Creates an ingredient for an item that must have no NBT
   * @param name  Item name
   * @return  Ingredient
   */
  public static NBTNameIngredient from(ResourceLocation name) {
    return new NBTNameIngredient(name, null);
  }

  @Override
  public boolean test(@Nullable ItemStack stack) {
    throw new UnsupportedOperationException();
  }

  @Override
  public JsonElement toJson() {
    JsonObject json = new JsonObject();
    json.addProperty(CustomIngredientImpl.TYPE_KEY, NbtIngredient.SERIALIZER.getIdentifier().toString());
    JsonObject item = new JsonObject();
    item.addProperty("item", name.toString());

    json.add("base", item);
    json.addProperty("strict", true);

    if (nbt != null) {
      json.add("nbt", NbtOps.INSTANCE.convertTo(JsonOps.INSTANCE, nbt));
    }

    return json;
  }
}
