package slimeknights.mantle.lib.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.entity.player.Player;

public interface FOVModifierCallback {
  Event<FOVModifierCallback> EVENT = EventFactory.createArrayBacked(FOVModifierCallback.class, callbacks -> (player, fov) -> {
    for(FOVModifierCallback e : callbacks) {
      float newFov = e.getNewFOV(player, fov);
      if(newFov != fov)
        return newFov;
    }
    return fov;
  });

  float getNewFOV(Player entity, float fov);
}
