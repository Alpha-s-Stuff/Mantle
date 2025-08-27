package slimeknights.mantle.data.predicate;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import lombok.RequiredArgsConstructor;
import net.minecraft.resources.ResourceLocation;
import slimeknights.mantle.Mantle;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.mantle.util.typed.TypedMap;

import java.util.function.Function;

/** Predicate registry that upon failure to find a predicate type will fallback to the fallback type */
public class FallbackPredicateRegistry<T,F> extends PredicateRegistry<T> {
  private final Function<T,F> getter;
  private final PredicateRegistry<F> fallback;
  private final RecordLoadable<FallbackPredicate> fallbackLoader;

  /**
   * Creates a new instance
   * @param defaultInstance Default instance, typically expected to be an any predicate.
   */
  public FallbackPredicateRegistry(String name, IJsonPredicate<T> defaultInstance, PredicateRegistry<F> fallback, Function<T,F> getter, String fallbackName) {
    super(name, defaultInstance);
    this.fallback = fallback;
    this.getter = getter;
    this.fallbackLoader = RecordLoadable.create(fallback.directField(fallbackName + "_type", p -> p.predicate), FallbackPredicate::new);
    this.register(Mantle.getResource(fallbackName), fallbackLoader);
  }

  /** Creates a fallback predicate instance */
  public IJsonPredicate<T> fallback(IJsonPredicate<F> predicate) {
    return new FallbackPredicate(predicate);
  }

  @Override
  public IJsonPredicate<T> convert(JsonElement element, String key, TypedMap context) {
    if (element.isJsonNull()) {
      return getDefault();
    }
    // identify type key, and the object we will load from
    if (element.isJsonObject()) {
      return deserialize(element.getAsJsonObject(), context);
    } else if (compact && element.isJsonPrimitive()) {
      ResourceLocation type = JsonHelper.convertToResourceLocation(element, "type");
      //  see if we have a primary loader, if so parse that
      RecordLoadable<? extends IJsonPredicate<T>> loader = loaders.getValue(type);
      if (loader != null) {
        EMPTY_OBJECT.entrySet().clear();
        return loader.deserialize(EMPTY_OBJECT, context);
      }
      return new FallbackPredicate(this.fallback.convert(element, key, context));
    } else {
      throw new JsonSyntaxException("Invalid " + getName() + " JSON at " + key + ", must be a JSON object" + (compact ? " or a string" : ""));
    }
  }

  @Override
  public IJsonPredicate<T> deserialize(JsonObject json, TypedMap context) {
    ResourceLocation type = JsonHelper.getResourceLocation(json, "type");
    //  see if we have a primary loader, if so parse that
    RecordLoadable<? extends IJsonPredicate<T>> loader = loaders.getValue(type);
    if (loader != null) {
      return loader.deserialize(json, context);
    }
    // primary loader failed, try a fallback loader
    return new FallbackPredicate(this.fallback.deserialize(json, context));
  }

  @SuppressWarnings("unchecked")
  @Override
  public JsonElement serialize(IJsonPredicate<T> src) {
    // write the fallback directly to JSON instead of as a nested type
    if (src instanceof NestedPredicate<?>) {
      return this.fallback.serialize(((NestedPredicate<F>)src).predicate());
    }
    return super.serialize(src);
  }

  @SuppressWarnings("unchecked")
  @Override
  public void serialize(IJsonPredicate<T> src, JsonObject json) {
    if (src instanceof NestedPredicate<?>) {
      this.fallback.serialize(((NestedPredicate<F>)src).predicate(), json);
    } else {
      super.serialize(src, json);
    }
  }

  /** Helper interface to make the cast work */
  private interface NestedPredicate<F> {
    IJsonPredicate<F> predicate();
  }

  /** Predicate matching another predicate type */
  @RequiredArgsConstructor
  public class FallbackPredicate implements IJsonPredicate<T>, NestedPredicate<F> {
    private final IJsonPredicate<F> predicate;

    @Override
    public IJsonPredicate<F> predicate() {
      return predicate;
    }

    @Override
    public boolean matches(T input) {
      return predicate.matches(getter.apply(input));
    }

    @Override
    public IJsonPredicate<T> inverted() {
      return invert(this);
    }

    @Override
    public RecordLoadable<? extends IJsonPredicate<T>> getLoader() {
      return fallbackLoader;
    }
  }
}
