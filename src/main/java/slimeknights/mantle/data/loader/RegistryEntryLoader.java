package slimeknights.mantle.data.loader;

import com.google.gson.JsonObject;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import slimeknights.mantle.data.GenericLoaderRegistry.IGenericLoader;
import slimeknights.mantle.data.GenericLoaderRegistry.IHaveLoader;
import slimeknights.mantle.util.JsonHelper;

import java.util.Objects;
import java.util.function.Function;

/**
 * Serializer for an object with a registry entry parameter
 * @param <O>  Object type
 * @param <V>  Registry entry type
 * @see RegistrySetLoader
 */
public record RegistryEntryLoader<O extends IHaveLoader<?>,V>(
  String key,
  Registry<V> registry,
  Function<V,O> constructor,
  Function<O,V> getter
) implements IGenericLoader<O> {

  @Override
  public O deserialize(JsonObject json) {
    return constructor.apply(JsonHelper.getAsEntry(registry, json, key));
  }

  @Override
  public void serialize(O object, JsonObject json) {
    json.addProperty(key, Objects.requireNonNull(registry.getKey(getter.apply(object))).toString());
  }

  @Override
  public O fromNetwork(FriendlyByteBuf buffer) {
    return constructor.apply(registry.byId(buffer.readVarInt()));
  }

  @Override
  public void toNetwork(O object, FriendlyByteBuf buffer) {
    buffer.writeVarInt(registry.getId(getter.apply(object)));
  }
}
