package slimeknights.mantle.data;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import net.minecraft.world.item.crafting.Ingredient;

import java.lang.reflect.Type;

public class IngredientSerializer implements JsonSerializer<Ingredient>, JsonDeserializer<Ingredient> {

  public static final IngredientSerializer INSTANCE = new IngredientSerializer();

  @Override
  public Ingredient deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
    return Ingredient.fromJson(json);
  }

  @Override
  public JsonElement serialize(Ingredient src, Type typeOfSrc, JsonSerializationContext context) {
    return src.toJson();
  }
}
