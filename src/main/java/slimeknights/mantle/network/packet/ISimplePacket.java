package slimeknights.mantle.network.packet;

import io.github.fabricators_of_create.porting_lib.util.NetworkDirection;
import me.pepperbell.simplenetworking.C2SPacket;
import me.pepperbell.simplenetworking.S2CPacket;
import me.pepperbell.simplenetworking.SimpleChannel;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvironmentInterface;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.PacketListener;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.util.thread.BlockableEventLoop;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

/**
 * Packet interface to add common methods for registration
 */
@EnvironmentInterface(value = EnvType.CLIENT, itf = S2CPacket.class)
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
    handle(new Context(server, handler, player, channel));
  }

  @Environment(EnvType.CLIENT)
  @Override
  default void handle(Minecraft client, ClientPacketListener listener, PacketSender responseSender, SimpleChannel channel) {
    handle(new Context(client, listener, null, channel));
  }

  public record Context(BlockableEventLoop<?> executor, PacketListener handler, @Nullable ServerPlayer sender, SimpleChannel channel) implements Supplier<Context> {
    public CompletableFuture<Void> enqueueWork(Runnable runnable) {
      // Must check ourselves as Minecraft will sometimes delay tasks even when they are received on the client thread
      // Same logic as ThreadTaskExecutor#runImmediately without the join
      if (!executor.isSameThread()) {
        return executor.submitAsync(runnable); // Use the internal method so thread check isn't done twice
      } else {
        runnable.run();
        return CompletableFuture.completedFuture(null);
      }
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
