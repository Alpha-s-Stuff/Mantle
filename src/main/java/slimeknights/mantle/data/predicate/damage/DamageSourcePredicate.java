package slimeknights.mantle.data.predicate.damage;

import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import slimeknights.mantle.data.GenericLoaderRegistry;
import slimeknights.mantle.data.GenericLoaderRegistry.IGenericLoader;
import slimeknights.mantle.data.predicate.AndJsonPredicate;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.mantle.data.predicate.InvertedJsonPredicate;
import slimeknights.mantle.data.predicate.NestedJsonPredicateLoader;
import slimeknights.mantle.data.predicate.OrJsonPredicate;

import java.util.function.Predicate;

import static slimeknights.mantle.data.GenericLoaderRegistry.SingletonLoader.singleton;

/**
 * Predicate testing for damage sources
 */
public interface DamageSourcePredicate extends IJsonPredicate<DamageSource> {
  /** Predicate that matches all sources */
  DamageSourcePredicate ANY = simple(source -> true);
  /** Loader for item predicates */
  GenericLoaderRegistry<IJsonPredicate<DamageSource>> LOADER = new GenericLoaderRegistry<>(ANY, true);
  /** Loader for inverted conditions */
  InvertedJsonPredicate.Loader<DamageSource> INVERTED = new InvertedJsonPredicate.Loader<>(LOADER, false);
  /** Loader for and conditions */
  NestedJsonPredicateLoader<DamageSource,AndJsonPredicate<DamageSource>> AND = AndJsonPredicate.createLoader(LOADER, INVERTED);
  /** Loader for or conditions */
  NestedJsonPredicateLoader<DamageSource,OrJsonPredicate<DamageSource>> OR = OrJsonPredicate.createLoader(LOADER, INVERTED);

  /* Vanilla getters */
  DamageSourcePredicate PROJECTILE = simple(damageSource -> damageSource.is(DamageTypeTags.IS_PROJECTILE));
  DamageSourcePredicate EXPLOSION = simple(damageSource -> damageSource.is(DamageTypeTags.IS_EXPLOSION));
  DamageSourcePredicate BYPASS_ARMOR = simple(damageSource -> damageSource.is(DamageTypeTags.BYPASSES_ARMOR));
  DamageSourcePredicate DAMAGE_HELMET = simple(damageSource -> damageSource.is(DamageTypeTags.DAMAGES_HELMET));
  DamageSourcePredicate BYPASS_INVULNERABLE = simple(damageSource -> damageSource.is(DamageTypeTags.BYPASSES_INVULNERABILITY));
  DamageSourcePredicate BYPASS_MAGIC = simple(damageSource -> damageSource.is(DamageTypeTags.BYPASSES_EFFECTS));
  DamageSourcePredicate FIRE = simple(damageSource -> damageSource.is(DamageTypeTags.IS_FIRE));
  DamageSourcePredicate MAGIC = simple(damageSource -> damageSource.is(DamageTypes.MAGIC));
  DamageSourcePredicate FALL = simple(damageSource -> damageSource.is(DamageTypeTags.IS_FALL));

  /** Damage that protection works against */
  DamageSourcePredicate CAN_PROTECT = simple(source -> !source.is(DamageTypeTags.BYPASSES_EFFECTS) && !source.is(DamageTypeTags.BYPASSES_INVULNERABILITY));
  /** Custom concept: damage dealt by non-projectile entities */
  DamageSourcePredicate MELEE = simple(source -> {
    if (source.is(DamageTypeTags.IS_PROJECTILE)) {
      return false;
    }
    // if it's caused by an entity, require it to simply not be thorns
    // meets most normal melee attacks, like zombies, but also means a melee fire or melee magic attack will work
    if (source.getEntity() != null) {
      return !source.is(DamageTypes.THORNS);
    } else {
      // for non-entity damage, require it to not be any other type
      // blocks fall damage, falling blocks, cactus, but not starving, drowning, freezing
      return !source.is(DamageTypeTags.BYPASSES_ARMOR) && !source.is(DamageTypeTags.IS_FIRE) && !source.is(DamageTypes.MAGIC) && !source.is(DamageTypeTags.IS_EXPLOSION);
    }
  });

  @Override
  default IJsonPredicate<DamageSource> inverted() {
    return INVERTED.create(this);
  }

  /** Creates a simple predicate with no parameters */
  static DamageSourcePredicate simple(Predicate<DamageSource> predicate) {
    return singleton(loader -> new DamageSourcePredicate() {
      @Override
      public boolean matches(DamageSource source) {
        return predicate.test(source);
      }

      @Override
      public IGenericLoader<? extends IJsonPredicate<DamageSource>> getLoader() {
        return loader;
      }
    });
  }
}
