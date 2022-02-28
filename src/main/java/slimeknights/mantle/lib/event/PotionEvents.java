package slimeknights.mantle.lib.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import slimeknights.mantle.lib.util.MantleEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class PotionEvents extends MantleEvent.EntityEvent {

  public static Event<PotionAdded> POTION_ADDED = EventFactory.createArrayBacked(PotionAdded.class, callbacks -> event -> {
    for(PotionAdded e : callbacks)
      e.onPotionAdded(event);
  });

  public static Event<PotionApplicable> POTION_APPLICABLE = EventFactory.createArrayBacked(PotionApplicable.class, callbacks -> event -> {
    for(PotionApplicable e : callbacks)
      e.onPotionApplicable(event);
  });

  @Nullable
  protected final MobEffectInstance effect;

  public PotionEvents(Entity entity, MobEffectInstance effect) {
    super(entity);
    this.effect = effect;
  }

  @Nullable
  public MobEffectInstance getPotionEffect()
  {
    return effect;
  }

  public static class PotionApplicableEvent extends PotionEvents
  {
    public PotionApplicableEvent(LivingEntity living, MobEffectInstance effect)
    {
      super(living, effect);
    }

    /**
     * @return the PotionEffect.
     */
    @Override
    @Nonnull
    public MobEffectInstance getPotionEffect()
    {
      return super.getPotionEffect();
    }

    @Override
    public void sendEvent() {
      POTION_APPLICABLE.invoker().onPotionApplicable(this);
    }
  }

  public static class PotionAddedEvent extends PotionEvents
  {
    private final MobEffectInstance oldEffect;
    private final Entity source;

    public PotionAddedEvent(LivingEntity living, MobEffectInstance oldEffect, MobEffectInstance newEffect, Entity source)
    {
      super(living, newEffect);
      this.oldEffect = oldEffect;
      this.source = source;
    }

    /**
     * @return the added PotionEffect. This is the umerged PotionEffect if the old PotionEffect is not null.
     */
    @Override
    @Nonnull
    public MobEffectInstance getPotionEffect()
    {
      return super.getPotionEffect();
    }

    /**
     * @return the old PotionEffect. THis can be null if the entity did not have an effect of this kind before.
     */
    @Nullable
    public MobEffectInstance getOldPotionEffect()
    {
      return oldEffect;
    }

    /**
     * Returns the entity source of the effect, or {@code null} if none exists.
     *
     * @return the entity source of the effect, or {@code null}
     */
    @Nullable
    public Entity getPotionSource()
    {
      return source;
    }

    @Override
    public void sendEvent() {
      POTION_ADDED.invoker().onPotionAdded(this);
    }
  }

  @FunctionalInterface
  public interface PotionAdded {
    void onPotionAdded(PotionAddedEvent event);
  }

  @FunctionalInterface
  public interface PotionApplicable {
    void onPotionApplicable(PotionApplicableEvent event);
  }
}
