package slimeknights.mantle.lib.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;

public abstract class MantleEvent {
  private boolean canceled = false;
  private InteractionResult result = InteractionResult.PASS;

  public boolean isCanceled() {
    return canceled;
  }

  public void setCanceled(boolean canceled) {
    this.canceled = canceled;
  }

  /**
   * Returns the value set as the result of this event
   */
  public InteractionResult getResult()
  {
    return result;
  }

  /**
   * Sets the result value for this event, not all events can have a result set, and any attempt to
   * set a result for a event that isn't expecting it will result in a IllegalArgumentException.
   *
   * The functionality of setting the result is defined on a per-event bases.
   *
   * @param value The new result
   */
  public void setResult(InteractionResult value)
  {
    result = value;
  }

  public abstract void sendEvent();

  @AllArgsConstructor
  public abstract static class EntityEvent extends MantleEvent {
    @Getter
    private Entity entity;
  }

  public abstract static class EntityTeleportEvent extends EntityEvent {
    @Getter @Setter
    private double targetX, targetY, targetZ;

    public EntityTeleportEvent(Entity entity, double targetX, double targetY, double targetZ) {
      super(entity);
      this.targetX = targetX;
      this.targetY = targetY;
      this.targetZ = targetZ;
    }
  }
}
