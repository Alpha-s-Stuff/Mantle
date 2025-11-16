package slimeknights.mantle.recipe.helper;

import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredient;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredientSerializer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import slimeknights.mantle.data.loadable.record.RecordLoadable;

/** Ingredient serializer made using loadables */
public record LoadableIngredientSerializer<T extends CustomIngredient>(ResourceLocation id, RecordLoadable<T> loadable) implements CustomIngredientSerializer<T> {
  @Override
  public T read(FriendlyByteBuf buffer) {
    return loadable.decode(buffer);
  }

  @Override
  public ResourceLocation getIdentifier() {
    return id;
  }

  @Override
  public T read(JsonObject json) {
    return loadable.deserialize(json);
  }

  @Override
  public void write(FriendlyByteBuf buffer, T ingredient) {
    loadable.encode(buffer, ingredient);
  }

  /** Serializes the ingredient to JSON */
  @Override
  public void write(JsonObject json, T ingredient) {
    loadable.serialize(ingredient, json);
  }
}
