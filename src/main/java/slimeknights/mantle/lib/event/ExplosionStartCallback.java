package slimeknights.mantle.lib.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;

public interface ExplosionStartCallback {
  Event<ExplosionStartCallback> EVENT = EventFactory.createArrayBacked(ExplosionStartCallback.class, callbacks -> ((world, explosion) -> {
    for(ExplosionStartCallback event : callbacks)
      if(event.onExplosionStart(world, explosion))
        return true;
    return false;
  }));

    boolean onExplosionStart(Level world, Explosion explosion);
}
