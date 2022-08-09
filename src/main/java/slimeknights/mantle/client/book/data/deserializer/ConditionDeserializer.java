package slimeknights.mantle.client.book.data.deserializer;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import net.fabricmc.fabric.api.resource.conditions.v1.ConditionJsonProvider;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import slimeknights.mantle.client.book.data.JsonCondition;

import java.lang.reflect.Type;

/** Serializer for a forge condition */
public interface ConditionDeserializer {
  ConditionDeserializer.Deserializer DESERIALIZER = new ConditionDeserializer.Deserializer();
  ConditionDeserializer.Serializer SERIALIZER = new ConditionDeserializer.Serializer();

  class Deserializer implements JsonDeserializer<JsonCondition> {
    @Override
    public JsonCondition deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
      JsonObject jsonObject = GsonHelper.convertToJsonObject(json, ResourceConditions.CONDITIONS_KEY);
      if (jsonObject.has(ResourceConditions.CONDITIONS_KEY)) {
        JsonArray jsonCondition = jsonObject.getAsJsonArray(ResourceConditions.CONDITIONS_KEY);
        ResourceLocation id = new ResourceLocation(jsonCondition.get(0).getAsJsonObject().get(ResourceConditions.CONDITION_ID_KEY).getAsString());
        return new JsonCondition(id, jsonObject);
      }
      return new JsonCondition();
    }
  }

  class Serializer implements JsonSerializer<JsonCondition> {
    @Override
    public JsonElement serialize(JsonCondition src, Type typeOfSrc, JsonSerializationContext context) {
      if (src.getConditionJsonProvider() != null) {
        JsonObject conditions = new JsonObject();
        ConditionJsonProvider.write(conditions, src.getConditionJsonProvider());
        return conditions;
      }
      return null;
    }
  }
}

