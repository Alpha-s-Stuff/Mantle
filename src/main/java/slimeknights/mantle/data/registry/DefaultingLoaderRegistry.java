package slimeknights.mantle.data.registry;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.network.FriendlyByteBuf;
import slimeknights.mantle.data.loadable.field.DefaultingField;
import slimeknights.mantle.data.loadable.field.LoadableField;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.registry.GenericLoaderRegistry.IHaveLoader;
import slimeknights.mantle.util.typed.TypedMap;

import java.lang.reflect.Type;
import java.util.function.Function;

/**
 * Extension of {@link GenericLoaderRegistry} with a default instance used for null or missing fields.
 * @param <T>
 */
public class DefaultingLoaderRegistry<T extends IHaveLoader> extends GenericLoaderRegistry<T> {
  /** Default instance, used for null values instead of null */
  private final T defaultInstance;
  public DefaultingLoaderRegistry(String name, T defaultInstance, boolean compact) {
    super(name, compact);
    this.defaultInstance = defaultInstance;
  }

  /** Gets the default value in this registry */
  public T getDefault() {
    return defaultInstance;
  }


  /* Default in JSON */

  @Override
  public T convert(JsonElement element, String key, TypedMap context) {
    if (element.isJsonNull()) {
      return defaultInstance;
    }
    return super.convert(element, key, context);
  }

  @Override
  public JsonElement serialize(T src, Type typeOfSrc, JsonSerializationContext context) {
    if (src == defaultInstance) {
      return JsonNull.INSTANCE;
    }
    return serialize(src);
  }

  /**
   * Gets then deserializes the given field, or returns the default value if missing.
   * @param parent  Parent to fetch field from
   * @param key     Field to get
   * @return  Value or default.
   */
  public T getOrDefault(JsonObject parent, String key, TypedMap context) {
    return super.getOrDefault(parent, key, defaultInstance, context);
  }

  /** Same as {@link #getOrDefault(JsonObject, String, TypedMap)} but uses {@link TypedMap#EMPTY} as the context. */
  public T getOrDefault(JsonObject parent, String key) {
    return getOrDefault(parent, key, TypedMap.EMPTY);
  }


  /* Default in network */

  @SuppressWarnings("unchecked")  // the cast is safe here as its just doing a map lookup, shouldn't cause harm if it fails. Besides, the loader has to extend T to work
  @Override
  public void encode(FriendlyByteBuf buffer, T src) {
    if (src == defaultInstance) {
      loaders.encodeOptional(buffer, null);
      return;
    }
    RecordLoadable<? extends IHaveLoader> loader = src.getLoader();
    loaders.encodeOptional(buffer, (RecordLoadable<? extends T>)loader);
    encode(loader, buffer, src);
  }

  @Override
  public T decode(FriendlyByteBuf buffer, TypedMap context) {
    RecordLoadable<? extends T> loader = loaders.decodeOptional(buffer);
    if (loader == null) {
      return defaultInstance;
    }
    return loader.decode(buffer, context);
  }


  /* Defaulting fields */

  /**
   * Creates a defaulting for this registry, using the internal default instance as the default
   * @param key               Json key
   * @param serializeDefault  If true, serializes the default instance. If false skips it
   * @param getter            Getter function
   * @param <P>  Field target
   * @return  Defaulting field instance
   */
  public <P> LoadableField<T,P> defaultField(String key, boolean serializeDefault, Function<P,T> getter) {
    return new DefaultingField<>(this, key, defaultInstance, serializeDefault, getter);
  }

  /** Creates a defaulting field that does not serialize */
  public <P> LoadableField<T,P> defaultField(String key, Function<P,T> getter) {
    return defaultField(key, false, getter);
  }
}
