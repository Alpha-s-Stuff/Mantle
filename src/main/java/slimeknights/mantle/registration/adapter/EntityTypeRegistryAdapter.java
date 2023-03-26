package slimeknights.mantle.registration.adapter;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import slimeknights.mantle.Mantle;

/**
 * Registry adapter for registering entity types
 */
@SuppressWarnings("unused")
public class EntityTypeRegistryAdapter extends RegistryAdapter<EntityType<?>> {
  /** @inheritDoc */
  public EntityTypeRegistryAdapter(String modId) {
    super(BuiltInRegistries.ENTITY_TYPE, modId);
  }

  /** @inheritDoc */
  public EntityTypeRegistryAdapter() {
    super(BuiltInRegistries.ENTITY_TYPE, Mantle.modId);
  }

  /**
   * Registers an entity type from a builder
   * @param builder  Builder instance
   * @param name     Type name
   * @param <T>      Entity type
   * @return  Registered entity type
   */
  public <T extends Entity> EntityType<T> register(FabricEntityTypeBuilder<T> builder, String name) {
    return register(builder.build(), name);
  }
}
