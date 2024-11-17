package slimeknights.mantle.network;

import io.github.fabricators_of_create.porting_lib.util.NetworkDirection;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

import slimeknights.mantle.network.packet.ISimplePacket;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.function.BiConsumer;

/**
 * A small network implementation/wrapper using AbstractPackets instead of IMessages.
 * Instantiate in your mod class and register your packets accordingly.
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class NetworkWrapper {
  private static final String PROTOCOL_VERSION = Integer.toString(1);

  /**
   * Registers a new {@link ISimplePacket}
   * @param type   Packet type
   * @param codec  Packet decoder, typically the constructor
   * @param <MSG>  Packet class type
   */
  public static <MSG extends ISimplePacket> void registerPacket(CustomPacketPayload.Type<MSG> type, StreamCodec<RegistryFriendlyByteBuf, MSG> codec, @Nullable NetworkDirection direction) {
    registerPacket(type, codec, ISimplePacket::handle, direction);
  }

  /**
   * Registers a new generic packet
   * @param type       Packet type
   * @param codec      Packet decoder, typically the constructor
   * @param consumer   Logic to handle a packet
   * @param direction  Network direction for validation. Pass null for no direction
   * @param <MSG>  Packet class type
   */
  public static <MSG extends ISimplePacket> void registerPacket(CustomPacketPayload.Type<MSG> type, StreamCodec<RegistryFriendlyByteBuf, MSG> codec, BiConsumer<MSG,ISimplePacket.Context> consumer, @Nullable NetworkDirection direction) {
    if (direction == NetworkDirection.PLAY_TO_CLIENT) {
      PayloadTypeRegistry.playS2C().register(type, codec);
      ServerPlayNetworking.registerGlobalReceiver(type, (payload, context) -> new ISimplePacket.Context(context.server(), context.player(), context.responseSender()));
    } else {
      PayloadTypeRegistry.playC2S().register(type, codec);
      ClientPlayNetworking.registerGlobalReceiver(type, (payload, context) -> new ISimplePacket.Context(context.client(), null, context.responseSender()));
    }
  }


  /* Sending packets */

  /**
   * Sends a packet to the server
   * @param msg  Packet to send
   */
  public static void sendToServer(ISimplePacket msg) {
    ClientPlayNetworking.send(msg);
  }

  /**
   * Sends a vanilla packet to the given entity
   * @param player  Player receiving the packet
   * @param packet  Packet
   */
  public static void sendVanillaPacket(Packet<?> packet, Entity player) {
    if (player instanceof ServerPlayer sPlayer) {
      sPlayer.connection.send(packet);
    }
  }

  /**
   * Sends a packet to a player
   * @param msg     Packet
   * @param player  Player to send
   */
  public static void sendTo(CustomPacketPayload msg, Player player) {
    if (player instanceof ServerPlayer) {
      ServerPlayNetworking.send((ServerPlayer) player, msg);
    }
  }

  /**
   * Sends a packet to a player
   * @param msg     Packet
   * @param player  Player to send
   */
  public static void sendTo(ISimplePacket msg, ServerPlayer player) {
    if (ServerPlayNetworking.canSend(player, msg.type())) {
      ServerPlayNetworking.send(player, msg);
    }
  }

  /**
   * Sends a packet to players near a location
   * @param msg          Packet to send
   * @param serverWorld  World instance
   * @param position     Position within range
   */
  public static void sendToClientsAround(ISimplePacket msg, ServerLevel serverWorld, BlockPos position) {
    for (ServerPlayer player : PlayerLookup.tracking(serverWorld, position)) {
      if (ServerPlayNetworking.canSend(player, msg.type()))
        ServerPlayNetworking.send(player, msg);
    }
  }

  /**
   * Sends a packet to all entities tracking the given entity
   * @param msg     Packet
   * @param entity  Entity to check
   */
  public static void sendToTrackingAndSelf(CustomPacketPayload msg, Entity entity) {
    var players = PlayerLookup.tracking(entity);
    if (entity instanceof ServerPlayer player && !players.contains(player)) {
      players = new ArrayList<>(players);
      players.add(player);
    }
    for (var player : players) {
      if (ServerPlayNetworking.canSend(player, msg.type()))
        ServerPlayNetworking.send(player, msg);
    }
  }

  /**
   * Sends a packet to all entities tracking the given entity
   * @param msg     Packet
   * @param entity  Entity to check
   */
  public static void sendToTracking(ISimplePacket msg, Entity entity) {
    for (var player : PlayerLookup.tracking(entity)) {
      if (ServerPlayNetworking.canSend(player, msg.type()))
        ServerPlayNetworking.send(player, msg);
    }
  }
}
