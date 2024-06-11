package slimeknights.mantle.data.loader;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import slimeknights.mantle.data.registry.GenericLoaderRegistry.IGenericLoader;
import slimeknights.mantle.data.registry.GenericLoaderRegistry.IHaveLoader;
import slimeknights.mantle.util.JsonHelper;

import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

/**
 * Generic loader for reading a loadable object with a set of registry objects
 * @param <R>  Registry type
 * @param <T>  Loader object type
 * @see RegistryEntryLoader
 */
public record RegistrySetLoader<R, T extends IHaveLoader<?>>(
  String key,
  Registry<R> registry,
  Function<Set<R>, T> constructor,
  Function<T, Set<R>> getter
) implements IGenericLoader<T> {
  @Override
  public T deserialize(JsonObject json) {
    Set<R> set = ImmutableSet.copyOf(JsonHelper.parseList(json, key, (element, jsonKey) -> {
      ResourceLocation objectKey = JsonHelper.convertToResourceLocation(element, jsonKey);
      if (registry.containsKey(objectKey)) {
        return registry.get(objectKey);
      }
      throw new JsonSyntaxException("Unknown " + key + " '" + objectKey + "'");
    }));
    return constructor.apply(set);
  }

  @Override
  public void serialize(T object, JsonObject json) {
    JsonArray array = new JsonArray();
    for (R entry : getter.apply(object)) {
      array.add(Objects.requireNonNull(registry.getKey(entry)).toString());
    }
    json.add(key, array);
  }

  @Override
  public T fromNetwork(FriendlyByteBuf buffer) {
    ImmutableSet.Builder<R> builder = ImmutableSet.builder();
    int max = buffer.readVarInt();
    for (int i = 0; i < max; i++) {
      builder.add(registry.byId(buffer.readVarInt()));
    }
    return constructor.apply(builder.build());
  }

  @Override
  public void toNetwork(T object, FriendlyByteBuf buffer) {
    Set<R> set = getter.apply(object);
    buffer.writeVarInt(set.size());
    for (R entry : set) {
      buffer.writeVarInt(registry.getId(entry));
    }
  }
}
