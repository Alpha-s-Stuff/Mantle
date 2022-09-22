package slimeknights.mantle.fabric.event;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;

@Environment(EnvType.CLIENT)
public interface ClientRightClickAir {
  Event<ClientRightClickAir> EVENT = EventFactory.createArrayBacked(ClientRightClickAir.class, clientRightClickAirs -> (player, hand) -> {
    for (ClientRightClickAir event : clientRightClickAirs)
      event.click(player, hand);
  });

  // Copied from arch because I am lazy
  void click(Player player, InteractionHand hand);
}
