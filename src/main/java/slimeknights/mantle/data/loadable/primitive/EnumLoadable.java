package slimeknights.mantle.data.loadable.primitive;

import com.google.gson.JsonSyntaxException;
import net.minecraft.network.FriendlyByteBuf;
import slimeknights.mantle.data.loadable.Loadable;
import slimeknights.mantle.data.loadable.mapping.EnumMapLoadable;
import slimeknights.mantle.data.loadable.mapping.EnumSetLoadable;
import slimeknights.mantle.util.typed.TypedMap;

import java.util.Locale;
import java.util.Map;
import java.util.Set;

/** Loadable for an enum value */
public record EnumLoadable<E extends Enum<E>>(Class<E> enumClass, E[] allowedValues) implements StringLoadable<E> {
  public EnumLoadable(Class<E> enumClass) {
    this(enumClass, enumClass.getEnumConstants());
  }

  @Override
  public E parseString(String name, String key, TypedMap context) {
    for (E value : allowedValues) {
      if (value.name().toLowerCase(Locale.ROOT).equals(name)) {
        return value;
      }
    }
    throw new JsonSyntaxException("Invalid " + enumClass.getSimpleName() + " " + name);
  }

  @Override
  public String getString(E object) {
    return object.name().toLowerCase(Locale.ROOT);
  }

  @Override
  public E decode(FriendlyByteBuf buffer, TypedMap context) {
    return buffer.readEnum(enumClass);
  }

  @Override
  public void encode(FriendlyByteBuf buffer, E object) {
    buffer.writeEnum(object);
  }

  @Override
  public Loadable<Set<E>> set(int minSize) {
    return new EnumSetLoadable<>(this, minSize);
  }

  @Override
  public <V> Loadable<Map<E,V>> mapWithValues(Loadable<V> valueLoadable, int minSize) {
    return new EnumMapLoadable<>(this, valueLoadable, minSize);
  }
}
