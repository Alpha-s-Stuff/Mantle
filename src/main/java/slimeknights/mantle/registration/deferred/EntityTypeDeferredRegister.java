package slimeknights.mantle.registration.deferred;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;
import slimeknights.mantle.lib.util.MantleRegistry;
import slimeknights.mantle.lib.util.MantleSpawnEggItem;
import slimeknights.mantle.lib.util.RegistryObject;
import slimeknights.mantle.registration.ItemProperties;

import java.util.function.Supplier;

/**
 * Deferred register for an entity, building the type from a builder instance and adding an egg
 */
@SuppressWarnings("unused")
public class EntityTypeDeferredRegister extends DeferredRegisterWrapper<EntityType<?>> {

  private final MantleRegistry<Item> itemRegistry;
  public EntityTypeDeferredRegister(String modID) {
    super(Registry.ENTITY_TYPE, modID);
    itemRegistry = MantleRegistry.create(Registry.ITEM, modID);
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
  public <T extends Entity> RegistryObject<EntityType<T>> register(String name, Supplier<FabricEntityTypeBuilder<T>> sup) {
    return register.register(name, () -> sup.get().build());
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
  public <T extends Mob> RegistryObject<EntityType<T>> registerWithEgg(String name, Supplier<FabricEntityTypeBuilder<T>> sup, int primary, int secondary) {
    RegistryObject<EntityType<T>> object = register(name, sup);
    itemRegistry.register(name + "_spawn_egg", () -> new MantleSpawnEggItem(object, primary, secondary, ItemProperties.EGG_PROPS));
    return object;
  }
}
