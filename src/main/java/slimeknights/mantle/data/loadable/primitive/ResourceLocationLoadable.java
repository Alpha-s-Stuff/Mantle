package slimeknights.mantle.data.loadable.primitive;

import com.google.gson.JsonElement;
import net.minecraft.resources.ResourceLocation;
import slimeknights.mantle.data.loadable.Loadables;
import slimeknights.mantle.util.typed.TypedMap;

/**
 * Helper for the common case of making a string loadable that uses resource locations.
 * @param <T>
 * @see Loadables#RESOURCE_LOCATION
 */
@SuppressWarnings("unused")  // API
public interface ResourceLocationLoadable<T> extends StringLoadable<T> {
  /** This is just an alias to minimize mistakes from the statically inherited StringLoadable default. */
  StringLoadable<ResourceLocation> DEFAULT = Loadables.RESOURCE_LOCATION;

  /**
   * Converts this value from a resource location.
   * @param name   Location to parse
   * @param key    Json key containing the value used for exceptions only.
   * @param context  Additional parsing context, used notably by recipe serializers to store the ID and serializer.
   * @return  Converted value.'
   * @throws com.google.gson.JsonSyntaxException  If no value exists for that key
   */
  T fromKey(ResourceLocation name, String key, TypedMap context);

  /** Same as {@link #fromKey(ResourceLocation, String, TypedMap)} but passes {@link TypedMap#EMPTY} for context. */
  default T fromKey(ResourceLocation name, String key) {
    return fromKey(name, key, TypedMap.EMPTY);
  }

  @Override
  default T parseString(String value, String key, TypedMap context) {
    return fromKey(Loadables.RESOURCE_LOCATION.parseString(value, key), key, context);
  }

  @Override
  default T convert(JsonElement element, String key, TypedMap context) {
    return fromKey(Loadables.RESOURCE_LOCATION.convert(element, key, context), key, context);
  }

  /**
   * Converts this object to its serialized representation.
   * @param object  Object to serialize
   * @return  String representation of the object.
   * @throws RuntimeException  if unable to serialize this to a string
   */
  ResourceLocation getKey(T object);

  @Override
  default String getString(T object) {
    return getKey(object).toString();
  }
}
