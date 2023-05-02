package slimeknights.mantle.registration.deferred;

import io.github.fabricators_of_create.porting_lib.util.LazyRegistrar;
import io.github.fabricators_of_create.porting_lib.util.RegistryObject;
import lombok.RequiredArgsConstructor;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

import java.util.function.Supplier;

/** Deferred register instance that synchronizes register calls */
@RequiredArgsConstructor(staticName = "create")
public class SynchronizedDeferredRegister<T> {
  private final LazyRegistrar<T> internal;

  /** Creates a new instance for the given resource key */
  public static <T> SynchronizedDeferredRegister<T> create(ResourceKey<? extends Registry<T>> key, String modid) {
    return create(LazyRegistrar.create(key, modid));
  }

  /** Creates a new instance for the given forge registry */
  public static <B> SynchronizedDeferredRegister<B> create(Registry<B> registry, String modid) {
    return create(LazyRegistrar.create(registry, modid));
  }

  /** Registers the given object, synchronized over the internal register */
  public <I extends T> RegistryObject<I> register(final String name, final Supplier<? extends I> sup) {
    synchronized (internal) {
      return (RegistryObject<I>) internal.register(name, sup);
    }
  }

  /**
   * Registers the internal register with the event bus
   */
  public void register() {
    internal.register();
  }
}
