package slimeknights.mantle.lib.event;

import com.mojang.datafixers.DataFixerBuilder;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface RegisterDataFixerCallback {
  Event<RegisterDataFixerCallback> EVENT = EventFactory.createArrayBacked(RegisterDataFixerCallback.class, callbacks -> builder -> {
    for(RegisterDataFixerCallback e : callbacks)
      e.addDataFixers(builder);
  });

  void addDataFixers(DataFixerBuilder builder);
}
