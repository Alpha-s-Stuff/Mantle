package slimeknights.mantle.network.packet;

import io.github.fabricators_of_create.porting_lib.util.NetworkDirection;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

import javax.annotation.Nullable;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

/**
 * Packet interface to add common methods for registration
 */
public interface ISimplePacket extends CustomPacketPayload {
  /**
   * Handles receiving the packet
   * @param context  Packet context
   */
  void handle(Supplier<Context> context);

  public record Context(Executor exec, @Nullable ServerPlayer player, PacketSender sender) implements Supplier<Context> {
    public void enqueueWork(Runnable runnable) {
      exec().execute(runnable);
    }

    @Nullable
    public ServerPlayer getSender() {
      return player();
    }

    public NetworkDirection getDirection() {
      return player() == null ? NetworkDirection.PLAY_TO_SERVER : NetworkDirection.PLAY_TO_CLIENT;
    }

    public void setPacketHandled(boolean value) {
    }

    @Override
    public Context get() {
      return this;
    }
  }
}
