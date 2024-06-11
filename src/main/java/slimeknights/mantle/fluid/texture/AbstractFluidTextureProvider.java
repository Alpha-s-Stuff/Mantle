package slimeknights.mantle.fluid.texture;

import io.github.fabricators_of_create.porting_lib.fluids.FluidType;
import io.github.fabricators_of_create.porting_lib.fluids.PortingLibFluids;
import io.github.fabricators_of_create.porting_lib.util.RegistryObject;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.core.Registry;
import net.minecraft.data.CachedOutput;
import net.minecraft.server.packs.PackType;
import slimeknights.mantle.data.GenericDataProvider;
import slimeknights.mantle.registration.object.FluidObject;
import slimeknights.mantle.util.JsonHelper;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * Data provider for {@link FluidTexture}
 */
@SuppressWarnings("unused")
public abstract class AbstractFluidTextureProvider extends GenericDataProvider {
  private final Map<FluidType,FluidTexture.Builder> allTextures = new HashMap<>();
  private final Set<FluidType> ignore = new HashSet<>();
  @Nullable
  private final String modId;

  public AbstractFluidTextureProvider(FabricDataOutput output, @Nullable String modId) {
    super(output, PackType.CLIENT_RESOURCES, FluidTextureManager.FOLDER, JsonHelper.DEFAULT_GSON);
    this.modId = modId;
  }

  @Override
  public final CompletableFuture<?> run(CachedOutput cache) {
    addTextures();
    Registry<FluidType> fluidTypeRegistry = PortingLibFluids.FLUID_TYPES;

    // ensure we added textures for all our fluid types
    if (modId != null) {
      List<String> missing = fluidTypeRegistry.entrySet().stream().filter(entry -> entry.getKey().location().getNamespace().equals(modId) && !allTextures.containsKey(entry.getValue()) && !ignore.contains(entry.getValue())).map(e -> e.getKey().location().toString()).toList();
      if (!missing.isEmpty()) {
        throw new IllegalStateException("Missing fluid textures for: " + String.join(", ", missing));
      }
    }

    List<CompletableFuture<?>> futures = new ArrayList<>();

    // save files
    allTextures.forEach((type, data) -> saveJson(futures, cache, Objects.requireNonNull(fluidTypeRegistry.getKey(type)), data.build().serialize()));
    return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
  }

  /** Override to add your textures at the proper time */
  public abstract void addTextures();

  /** Create a new builder for the give fluid type */
  public FluidTexture.Builder texture(FluidType fluid) {
    return allTextures.computeIfAbsent(fluid, FluidTexture.Builder::new);
  }

  /** Create a new builder for the give fluid type */
  public FluidTexture.Builder texture(FluidObject<?> fluid) {
    return texture(fluid.getType());
  }

  /** Create a new builder for the give fluid type */
  public FluidTexture.Builder texture(RegistryObject<? extends FluidType> fluid) {
    return texture(fluid.get());
  }

  /** Marks the given fluid type to be ignored by this texture provider */
  public void skip(FluidType fluid) {
    ignore.add(fluid);
  }

  /** Marks the given fluid type to be ignored by this texture provider */
  public void skip(FluidObject<?> fluid) {
    skip(fluid.getType());
  }

  /** Marks the given fluid type to be ignored by this texture provider */
  public void skip(RegistryObject<? extends FluidType> fluid) {
    skip(fluid.get());
  }
}
