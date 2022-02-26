package slimeknights.mantle.lib.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.world.entity.Entity;

public class MantleEvent {
  private boolean canceled = false;

  public boolean isCanceled() {
    return canceled;
  }

  public void setCanceled(boolean canceled) {
    this.canceled = canceled;
  }

  @AllArgsConstructor
  public static class EntityEvent extends MantleEvent {
    @Getter
    private Entity entity;
  }
}
