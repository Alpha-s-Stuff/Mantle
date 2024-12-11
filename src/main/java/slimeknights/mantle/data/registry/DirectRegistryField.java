package slimeknights.mantle.data.registry;

import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import slimeknights.mantle.data.loadable.Loadable;
import slimeknights.mantle.data.loadable.field.AlwaysPresentLoadableField;
import slimeknights.mantle.data.registry.GenericLoaderRegistry.IHaveLoader;

import java.util.Map.Entry;
import java.util.function.Function;

/** Direct field for a registry, leaves type unchanged */
public record DirectRegistryField<T extends IHaveLoader,P>(Loadable<T> loadable, Function<P,T> getter) implements AlwaysPresentLoadableField<T,P> {
  /**
   * Serializes the passed object into the passed JSON
   * @param json      JSON target for serializing
   * @param loader    Loader for serializing the value
   * @param value     Value to serialized
   * @param <N>  Type of value
   */
  public static <N extends IHaveLoader> void serializeInto(JsonObject json, Loadable<N> loader, N value) {
    JsonElement element = loader.serialize(value);
    // if its an object, copy all the data over
    if (element.isJsonObject()) {
      JsonObject nestedObject = element.getAsJsonObject();
      for (Entry<String, JsonElement> entry : nestedObject.entrySet()) {
        json.add(entry.getKey(), entry.getValue());
      }
    } else if (element.isJsonPrimitive()){
      // if its a primitive, its the type ID, add just that by itself
      json.add("type", element);
    } else {
      throw new JsonIOException("Unable to serialize nested object, expected string or object");
    }
  }

  @Override
  public T get(JsonObject json) {
    return loadable.convert(json, "[unknown]");
  }

  @Override
  public void serialize(P parent, JsonObject json) {
    serializeInto(json, loadable, getter.apply(parent));
  }
}
