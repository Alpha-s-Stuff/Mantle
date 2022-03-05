package slimeknights.mantle.lib.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface ModelLoadCallback {
  Event<ModelLoadCallback> EVENT = EventFactory.createArrayBacked(ModelLoadCallback.class, callbacks -> () -> {
    for (ModelLoadCallback e : callbacks)
      e.onModelsStartLoading();
  });


  void onModelsStartLoading();
}
