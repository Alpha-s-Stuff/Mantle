package slimeknights.mantle.lib.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;

public interface PlayerBreakSpeedCallback {
  Event<PlayerBreakSpeedCallback> EVENT = EventFactory.createArrayBacked(PlayerBreakSpeedCallback.class, callbacks -> ((player, state, original, pos) -> {
    float newTime = original;
    for(PlayerBreakSpeedCallback event : callbacks)
      newTime = event.setBreakSpeed(player, state, original, pos);
    return newTime;
  }));

  float setBreakSpeed(Player player, BlockState state, float original, BlockPos pos);
}
