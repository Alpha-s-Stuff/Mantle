package slimeknights.mantle.registration;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.world.level.block.state.properties.WoodType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RegistrationHelper {
  /** Wood types to register with the texture atlas */
  private static final List<WoodType> WOOD_TYPES = new ArrayList<>();

  /**
   * Used to mark injected registry objects, as despite being set to null they will be nonnull at runtime.
   * @param <T>  Class type
   * @return  Null, its a lie
   */
  @SuppressWarnings("ConstantConditions")
  public static <T> T injected() {
    return null;
  }

  /**
   * Creates a supplier for a specific registry entry instance based on the delegate to a general instance.
   * Note that this performs an unchecked cast, be certain that the right type is returned
   * @param delegate  Delegate instance
   * @param <I>  Forge registry type
   * @return  Supplier for the given instance
   */
  @SuppressWarnings("unchecked")
  public static <I> Supplier<I> castDelegate(I delegate) {
    return () -> delegate;
  }

//  /**
//   * Handles missing mappings for the given registry
//   * @param event    Mappings event
//   * @param handler  Mapping handler
//   * @param <T>      Event type
//   */
//  public static <T extends IForgeRegistryEntry<T>> void handleMissingMappings(MissingMappings<T> event, String modID, Function<String, T> handler) {
//    for (Mapping<T> mapping : event.getAllMappings()) {
//      if (modID.equals(mapping.key.getNamespace())) {
//        @Nullable T value = handler.apply(mapping.key.getPath());
//        if (value != null) {
//          mapping.remap(value);
//        }
//      }
//    }
//  }

  /** Registers a wood type to be injected into the atlas, should be called before client setup */
  public static void registerWoodType(WoodType type) {
    synchronized (WOOD_TYPES) {
      WOOD_TYPES.add(type);
      WoodType.register(type);
    }
  }

  /** Runs the given consumer for each wood type registered */
  public static void forEachWoodType(Consumer<WoodType> consumer) {
    WOOD_TYPES.forEach(consumer);
  }

}
