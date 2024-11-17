package slimeknights.mantle.registration.deferred;

import io.github.fabricators_of_create.porting_lib.util.DeferredHolder;
import io.github.fabricators_of_create.porting_lib.util.DeferredRegister;
import io.github.fabricators_of_create.porting_lib.util.DeferredSpawnEggItem;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;

import java.util.function.Supplier;

/**
 * Deferred register for an entity, building the type from a builder instance and adding an egg
 */
@SuppressWarnings("unused")
public class EntityTypeDeferredRegister extends DeferredRegisterWrapper<EntityType<?>, DeferredRegister<EntityType<?>>> {

  private final SynchronizedDeferredRegister<Item, DeferredRegister.Items> itemRegistry;
  public EntityTypeDeferredRegister(String modID) {
    super(DeferredRegister.create(Registries.ENTITY_TYPE, modID));
    itemRegistry = SynchronizedDeferredRegister.create(DeferredRegister.createItems(modID));
  }

  @Override
  public void register() {
    super.register();
    itemRegistry.register();
  }

  /**
   * Registers a entity type for the given entity type builder
   * @param name  Entity name
   * @param sup   Entity builder instance
   * @param <T>   Entity class type
   * @return  Entity registry object
   */
  public <T extends Entity> DeferredHolder<EntityType<?>, EntityType<T>> register(String name, Supplier<EntityType.Builder<T>> sup) {
    return register.register(name, () -> sup.get().build(name));
  }

  /**
   * Registers a entity type for the given entity type builder, and registers a spawn egg for it
   * @param name       Entity name
   * @param sup        Entity builder instance
   * @param primary    Primary egg color
   * @param secondary  Secondary egg color
   * @param <T>   Entity class type
   * @return  Entity registry object
   */
  public <T extends Mob> DeferredHolder<EntityType<?>, EntityType<T>> registerWithEgg(String name, Supplier<EntityType.Builder<T>> sup, int primary, int secondary) {
    DeferredHolder<EntityType<?>, EntityType<T>> object = register(name, sup);
    var spawnEgg = itemRegistry.register(name + "_spawn_egg", () -> new DeferredSpawnEggItem(object, primary, secondary, new Item.Properties()));
    ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.SPAWN_EGGS).register(entries -> entries.accept(spawnEgg.get()));
    return object;
  }
}
