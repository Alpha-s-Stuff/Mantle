package slimeknights.mantle.data;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.Registry;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;

import java.util.concurrent.CompletableFuture;

/** Tag provider for any registry from {@link net.minecraft.core.registries.BuiltInRegistries} that lacks a standard tag provider. */
public abstract class BuiltinRegistryTagProvider<T> extends IntrinsicHolderTagsProvider<T> {
  public BuiltinRegistryTagProvider(FabricDataOutput packOutput, Registry<T> registry, CompletableFuture<Provider> lookupProvider) {
    super(packOutput, registry.key(), lookupProvider,
      // not sure why fetching the resource key from the object is such a pain
      value -> registry.getHolder(registry.getId(value)).orElseThrow().key());
  }
}
