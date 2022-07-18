package slimeknights.mantle.client.book.data.deserializer;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

import java.lang.reflect.Type;
import java.util.function.Predicate;

public class ConditionDeserializer implements JsonDeserializer<Predicate<JsonObject>> {
  @Override
  public Predicate<JsonObject> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
    if(!json.isJsonObject())
      throw new JsonParseException("A condition must be a JSON Object");

    return ResourceConditions.get(ResourceLocation.tryParse(GsonHelper.getAsString(json.getAsJsonObject(), ResourceConditions.CONDITION_ID_KEY)));
  }
}
