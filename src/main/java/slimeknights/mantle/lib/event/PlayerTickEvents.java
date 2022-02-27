package slimeknights.mantle.lib.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

import net.minecraft.world.entity.player.Player;

public interface PlayerTickEvents {

  Event<Start> START = EventFactory.createArrayBacked(Start.class, callbacks -> (player) -> {
    for (Start callback : callbacks) {
      callback.onStartOfPlayerTick(player);
    }
  });

	Event<End> END = EventFactory.createArrayBacked(End.class, callbacks -> (player) -> {
		for (End callback : callbacks) {
			callback.onEndOfPlayerTick(player);
		}
	});

  @FunctionalInterface
  interface End {
    void onEndOfPlayerTick(Player player);
  }

  @FunctionalInterface
  interface Start {
    void onStartOfPlayerTick(Player player);
  }
}
