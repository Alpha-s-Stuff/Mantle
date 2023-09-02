package slimeknights.mantle.util;

import com.google.common.collect.ImmutableList;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Builder for creating a list by adding the last elements for the final list first
 */
public class ReversedListBuilder<E> {
  /** Store the list as a list of lists as that makes reversing easier */
  private final ImmutableList.Builder<Collection<E>> unpacked = ImmutableList.builder();

  /**
   * Adds all data from the given collection to the builder.
   * This is done in terms of collections rather than individual elements to speed up reversal, as the use of this adds elements in batches
   * @param collection  Collection to add
   */
  public void addAll(Collection<E> collection) {
    unpacked.add(collection);
  }

  public void add(E collection) {
    unpacked.add(Set.of(collection));
  }

  /** Builds the final list of quads */
  public ImmutableList<E> build() {
    List<Collection<E>> unpacked = this.unpacked.build();
    ImmutableList.Builder<E> packed = ImmutableList.builder();
    for (int i = unpacked.size() - 1; i >= 0; i--) {
      packed.addAll(unpacked.get(i));
    }
    return packed.build();
  }
}
