package slimeknights.mantle.network.packet;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.PacketListener;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.thread.BlockableEventLoop;
import slimeknights.mantle.network.channel.NetworkDirection;
import slimeknights.mantle.network.channel.SimpleChannel;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

/**
 * Packet interface to add common methods for registration
 */
public interface ISimplePacket {
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

  public record Context(BlockableEventLoop<?> executor, PacketListener handler, @Nullable ServerPlayer sender, NetworkDirection networkDirection, PacketSender packetDistributor, SimpleChannel channel) {
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
      return networkDirection;
    }

    public void setPacketHandled(boolean value) {
    }
  }
}
