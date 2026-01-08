package slimeknights.mantle.client.model;

import java.util.function.Predicate;

import com.google.common.base.Predicates;

// Reimplementing 1.20.1 forge code because I am lazy
public class ModelProperty<T> implements Predicate<T> {

  private final Predicate<T> predicate;

  public ModelProperty() {
    this(Predicates.alwaysTrue());
  }

  public ModelProperty(Predicate<T> predicate) {
    this.predicate = predicate;
  }

  @Override
  public boolean test(T t) {
    return predicate.test(t);
  }
}
