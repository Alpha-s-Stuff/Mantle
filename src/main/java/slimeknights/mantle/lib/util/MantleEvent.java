package slimeknights.mantle.lib.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.world.entity.Entity;

public abstract class MantleEvent {
  private boolean canceled = false;

  public boolean isCanceled() {
    return canceled;
  }

  public void setCanceled(boolean canceled) {
    this.canceled = canceled;
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
