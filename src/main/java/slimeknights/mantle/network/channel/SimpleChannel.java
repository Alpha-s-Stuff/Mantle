package slimeknights.mantle.network.channel;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectArrayMap;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.Connection;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import slimeknights.mantle.network.packet.ISimplePacket;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Porting Lib copy of {@link me.pepperbell.simplenetworking.SimpleChannel} that more closely matches forge and avoids unsafe client calls from annotation stripping
 */
public class SimpleChannel {

  private static final Logger LOGGER = LogManager.getLogger("Mantle Simple Networking");

  @Getter
  private final ResourceLocation channelName;
  private final Short2ObjectArrayMap<MessageHandler<?>> indicies = new Short2ObjectArrayMap<>();
  private final Object2ObjectArrayMap<Class<?>, MessageHandler<?>> types = new Object2ObjectArrayMap<>();

  public SimpleChannel(ResourceLocation channelName) {
    this.channelName = channelName;
  }

  public static void initServerListener(SimpleChannel channel) {
    ServerPlayNetworking.registerGlobalReceiver(channel.getChannelName(), new C2SHandler(channel));
  }

  @Environment(EnvType.CLIENT)
  public static void initClientListener(SimpleChannel channel) {
    ClientPlayNetworking.registerGlobalReceiver(channel.getChannelName(), new S2CHandler(channel));
  }

  public <MSG> MessageHandler<MSG> registerMessage(int index, Class<MSG> messageType, BiConsumer<MSG, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, MSG> decoder, BiConsumer<MSG, Supplier<ISimplePacket.Context>> messageConsumer) {
    return registerMessage(index, messageType, encoder, decoder, messageConsumer, Optional.empty());
  }

  public <MSG> MessageHandler<MSG> registerMessage(int index, Class<MSG> messageType, BiConsumer<MSG, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, MSG> decoder, BiConsumer<MSG, Supplier<ISimplePacket.Context>> messageConsumer, final Optional<NetworkDirection> networkDirection) {
    MessageHandler<MSG> handler = new MessageHandler<>(index, messageType, encoder, decoder, messageConsumer, networkDirection);
    indicies.put((short) (index & 0xff), handler);
    types.put(messageType, handler);
    return handler;
  }

  public <MSG> void encodeMessage(MSG message, FriendlyByteBuf target) {
    @SuppressWarnings("unchecked")
    MessageHandler<MSG> messageHandler = (MessageHandler<MSG>) types.get(message.getClass());
    if (messageHandler == null) {
      LOGGER.error("Received invalid message {} on channel {}", message.getClass().getName(), channelName);
      throw new IllegalArgumentException("Invalid message " + message.getClass().getName());
    }
    tryEncode(target, message, messageHandler);
  }

  public <MSG> FriendlyByteBuf toBuffer(MSG msg) {
    final FriendlyByteBuf bufIn = PacketByteBufs.create();
    encodeMessage(msg, bufIn);
    return bufIn;
  }

  public <MSG> Packet<?> toVanillaPacket(MSG message, NetworkDirection direction) {
    return direction.buildPacket(toBuffer(message), channelName);
  }

  public <MSG> void sendToServer(MSG message) {
    sendTo(message, Minecraft.getInstance().getConnection().getConnection(), NetworkDirection.PLAY_TO_SERVER);
  }

  public <MSG> void sendTo(MSG message, Connection manager, NetworkDirection direction) {
    manager.send(toVanillaPacket(message, direction));
  }

  /**
   * Send a message to the {@link PacketDistributor.PacketTarget} from a {@link PacketDistributor} instance.
   *
   * <pre>
   *     channel.send(PacketDistributor.PLAYER.with(()->player), message)
   * </pre>
   *
   * @param target  The curried target from a PacketDistributor
   * @param message The message to send
   * @param <MSG>   The type of the message
   */
  public <MSG> void send(PacketDistributor.PacketTarget target, MSG message) {
    target.send(toVanillaPacket(message, target.getDirection()));
  }

  public <MSG> void reply(MSG msgToReply, ISimplePacket.Context context) {
    context.packetDistributor().sendPacket(channelName, toBuffer(msgToReply));
  }

  public void receivePayload(FriendlyByteBuf buf, ISimplePacket.Context context) {
    short discriminator = buf.readUnsignedByte();
    MessageHandler<?> msgHandler = indicies.get(discriminator);
    if (msgHandler == null) {
      LOGGER.error("Received invalid discriminator byte {} on channel {}", discriminator, channelName);
      return;
    }
    tryDecode(buf, msgHandler, () -> context);
  }

  // Makes generics happy
  private static <M> void tryDecode(FriendlyByteBuf buf, MessageHandler<M> msgHandler, Supplier<ISimplePacket.Context> context) {
    msgHandler.decoder.map(d -> d.apply(buf))
      .ifPresent(m -> msgHandler.messageConsumer.accept(m, context));
  }

  private static <M> void tryEncode(FriendlyByteBuf target, M message, MessageHandler<M> codec) {
    codec.encoder.ifPresent(encoder -> {
      target.writeByte(codec.index & 0xff);
      encoder.accept(message, target);
    });
  }

  @AllArgsConstructor
  private static class C2SHandler implements ServerPlayNetworking.PlayChannelHandler {

    private final SimpleChannel channel;

    @Override
    public void receive(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl handler, FriendlyByteBuf buf, PacketSender responseSender) {

      ISimplePacket.Context context = new ISimplePacket.Context(server, handler, player, NetworkDirection.PLAY_TO_SERVER, responseSender, channel);
      channel.receivePayload(buf, context);
    }
  }

  @AllArgsConstructor
  @Environment(EnvType.CLIENT)
  private static class S2CHandler implements ClientPlayNetworking.PlayChannelHandler {

    private final SimpleChannel channel;

    @Override
    public void receive(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf, PacketSender responseSender) {
      ISimplePacket.Context context = new ISimplePacket.Context(client, handler, null, NetworkDirection.PLAY_TO_CLIENT, responseSender, channel);
      channel.receivePayload(buf, context);
    }
  }

  public class MessageHandler<MSG> {

    private final Optional<BiConsumer<MSG, FriendlyByteBuf>> encoder;
    private final Optional<Function<FriendlyByteBuf, MSG>> decoder;
    private final int index;
    private final BiConsumer<MSG, Supplier<ISimplePacket.Context>> messageConsumer;
    private final Class<MSG> messageType;
    private final Optional<NetworkDirection> networkDirection;

    public MessageHandler(int index, Class<MSG> messageType, BiConsumer<MSG, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, MSG> decoder, BiConsumer<MSG, Supplier<ISimplePacket.Context>> messageConsumer, final Optional<NetworkDirection> networkDirection) {
      this.index = index;
      this.messageType = messageType;
      this.encoder = Optional.ofNullable(encoder);
      this.decoder = Optional.ofNullable(decoder);
      this.messageConsumer = messageConsumer;
      this.networkDirection = networkDirection;
    }
  }
}
