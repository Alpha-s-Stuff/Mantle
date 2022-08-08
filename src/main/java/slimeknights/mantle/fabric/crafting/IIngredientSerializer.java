package slimeknights.mantle.fabric.crafting;

import com.google.gson.JsonObject;

import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.network.FriendlyByteBuf;

public interface IIngredientSerializer<T extends Ingredient> {
  T parse(FriendlyByteBuf buffer);

  T parse(JsonObject json);

  void write(FriendlyByteBuf buffer, T ingredient);
}
