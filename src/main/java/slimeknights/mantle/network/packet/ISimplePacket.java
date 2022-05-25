package slimeknights.mantle.network.packet;

import io.github.fabricators_of_create.porting_lib.util.NetworkDirection;
import me.pepperbell.simplenetworking.C2SPacket;
import me.pepperbell.simplenetworking.S2CPacket;
import me.pepperbell.simplenetworking.SimpleChannel;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.PacketListener;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

import javax.annotation.Nullable;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

/**
 * Packet interface to add common methods for registration
 */
public interface ISimplePacket extends S2CPacket, C2SPacket {
  /**
   * Encodes a packet for the buffer
   * @param buf  Buffer instance
   */
  void encode(FriendlyByteBuf buf);

  /**
   * Handles receiving the packet
   * @param context  Packet context
   */
  void handle(Supplier<Context> context);

  @Override
  default void handle(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl handler, PacketSender responseSender, SimpleChannel channel) {
    handle(new Context(server, handler, player, responseSender, channel));
  }

  @Override
  default void handle(Minecraft client, ClientPacketListener handler, PacketSender responseSender, SimpleChannel channel) {
    handle(new Context(client, handler, null, responseSender, channel));
  }

  public record Context(Executor exec, PacketListener handler, @Nullable ServerPlayer sender, PacketSender responseSender, SimpleChannel channel) implements Supplier<Context> {
    public void enqueueWork(Runnable runnable) {
      exec().execute(runnable);
    }

    @Nullable
    public ServerPlayer getSender() {
      return sender();
    }

    public NetworkDirection getDirection() {
      return sender() == null ? NetworkDirection.PLAY_TO_SERVER : NetworkDirection.PLAY_TO_CLIENT;
    }

    public void setPacketHandled(boolean value) {
    }

    @Override
    public Context get() {
      return this;
    }
  }
}
