package slimeknights.mantle.registration.deferred;

import io.github.fabricators_of_create.porting_lib.util.DeferredHolder;
import io.github.fabricators_of_create.porting_lib.util.DeferredRegister;
import lombok.RequiredArgsConstructor;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

import java.util.function.Supplier;

/** Deferred register instance that synchronizes register calls */
@RequiredArgsConstructor(staticName = "create")
public class SynchronizedDeferredRegister<T, R extends DeferredRegister<T>> {
  private final R internal;

  /** Creates a new instance for the given resource key */
  public static <T> SynchronizedDeferredRegister<T, DeferredRegister<T>> create(ResourceKey<? extends Registry<T>> key, String modid) {
    return new SynchronizedDeferredRegister<>(DeferredRegister.create(key, modid));
  }

  /** Creates a new instance for the given forge registry */
  public static <B> SynchronizedDeferredRegister<B, DeferredRegister<B>> create(Registry<B> registry, String modid) {
    return new SynchronizedDeferredRegister<>(DeferredRegister.create(registry, modid));
  }

  public static <T, R extends DeferredRegister<T>> SynchronizedDeferredRegister<T, R> create(R register) {
    return new SynchronizedDeferredRegister<>(register);
  }

  /** Registers the given object, synchronized over the internal register */
  public <I extends T> DeferredHolder<T, I> register(final String name, final Supplier<? extends I> sup) {
    synchronized (internal) {
      return internal.register(name, sup);
    }
  }

  /**
   * Registers the internal register with the event bus
   */
  public void register() {
    internal.register();
  }
}
