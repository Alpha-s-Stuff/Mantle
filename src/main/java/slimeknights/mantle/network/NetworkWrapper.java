package slimeknights.mantle.network;

import io.github.fabricators_of_create.porting_lib.util.NetworkDirection;
import me.pepperbell.simplenetworking.S2CPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.chunk.LevelChunk;

import me.pepperbell.simplenetworking.SimpleChannel;
import slimeknights.mantle.network.packet.ISimplePacket;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A small network implementation/wrapper using AbstractPackets instead of IMessages.
 * Instantiate in your mod class and register your packets accordingly.
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class NetworkWrapper {
  /** Network instance */
  public final SimpleChannel network;
  private int id = 0;
  private static final String PROTOCOL_VERSION = Integer.toString(1);

  /**
   * Creates a new network wrapper
   * @param channelName  Unique packet channel name
   */
  public NetworkWrapper(ResourceLocation channelName) {
    this.network = new SimpleChannel(channelName);
  }

  /**
   * Registers a new {@link ISimplePacket}
   * @param clazz    Packet class
   * @param decoder  Packet decoder, typically the constructor
   * @param <MSG>  Packet class type
   */
  public <MSG extends ISimplePacket> void registerPacket(Class<MSG> clazz, Function<FriendlyByteBuf, MSG> decoder, @Nullable NetworkDirection direction) {
    registerPacket(clazz, ISimplePacket::encode, decoder, ISimplePacket::handle, direction);
  }

  /**
   * Registers a new generic packet
   * @param clazz      Packet class
   * @param encoder    Encodes a packet to the buffer
   * @param decoder    Packet decoder, typically the constructor
   * @param consumer   Logic to handle a packet
   * @param direction  Network direction for validation. Pass null for no direction
   * @param <MSG>  Packet class type
   */
  public <MSG extends ISimplePacket> void registerPacket(Class<MSG> clazz, BiConsumer<MSG, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, MSG> decoder, BiConsumer<MSG,Supplier<ISimplePacket.Context>> consumer, @Nullable NetworkDirection direction) {
    if (direction == NetworkDirection.PLAY_TO_CLIENT) {
      this.network.registerS2CPacket(clazz, this.id++, decoder);
    } else {
      this.network.registerC2SPacket(clazz, this.id++, decoder);
    }
  }


  /* Sending packets */

  /**
   * Sends a packet to the server
   * @param msg  Packet to send
   */
  public void sendToServer(ISimplePacket msg) {
    this.network.sendToServer(msg);
  }

  /**
   * Sends a vanilla packet to the given entity
   * @param player  Player receiving the packet
   * @param packet  Packet
   */
  public void sendVanillaPacket(Packet<?> packet, Entity player) {
    if (player instanceof ServerPlayer sPlayer) {
      sPlayer.connection.send(packet);
    }
  }

  /**
   * Sends a packet to a player
   * @param msg     Packet
   * @param player  Player to send
   */
  public void sendTo(S2CPacket msg, Player player) {
    if (player instanceof ServerPlayer) {
      this.network.sendToClient(msg, (ServerPlayer) player);
    }
  }

  /**
   * Sends a packet to a player
   * @param msg     Packet
   * @param player  Player to send
   */
  public void sendTo(ISimplePacket msg, ServerPlayer player) {
//    if (!(player instanceof FakePlayer)) {
      network.sendToClient(msg, player);
//    }
  }

  /**
   * Sends a packet to players near a location
   * @param msg          Packet to send
   * @param serverWorld  World instance
   * @param position     Position within range
   */
  public void sendToClientsAround(ISimplePacket msg, ServerLevel serverWorld, BlockPos position) {
    network.sendToClientsTracking(msg, serverWorld, position);
  }

  /**
   * Sends a packet to all entities tracking the given entity
   * @param msg     Packet
   * @param entity  Entity to check
   */
  public void sendToTrackingAndSelf(S2CPacket msg, Entity entity) {
    this.network.sendToClientsTrackingAndSelf(msg, entity);
  }

  /**
   * Sends a packet to all entities tracking the given entity
   * @param msg     Packet
   * @param entity  Entity to check
   */
  public void sendToTracking(ISimplePacket msg, Entity entity) {
    this.network.sendToClientsTracking(msg, entity);
  }
}
