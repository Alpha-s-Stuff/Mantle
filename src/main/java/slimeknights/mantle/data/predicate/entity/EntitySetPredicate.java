package slimeknights.mantle.data.predicate.entity;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import slimeknights.mantle.data.GenericLoaderRegistry.IGenericLoader;
import slimeknights.mantle.data.loader.RegistrySetLoader;
import slimeknights.mantle.data.predicate.IJsonPredicate;

import java.util.Set;

/** Predicate matching entities from a set */
public record EntitySetPredicate(Set<EntityType<?>> entities) implements LivingEntityPredicate {
  public static final IGenericLoader<EntitySetPredicate> LOADER = new RegistrySetLoader<>("entities", BuiltInRegistries.ENTITY_TYPE, EntitySetPredicate::new, EntitySetPredicate::entities);

  @Override
  public boolean matches(LivingEntity entity) {
    return entities.contains(entity.getType());
  }

  @Override
  public IGenericLoader<? extends IJsonPredicate<LivingEntity>> getLoader() {
    return LOADER;
  }
}
