package slimeknights.mantle.network.channel;

import it.unimi.dsi.fastutil.objects.Reference2ReferenceArrayMap;
import net.fabricmc.api.EnvType;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ServerboundCustomPayloadPacket;
import net.minecraft.network.protocol.game.ClientboundCustomPayloadPacket;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum NetworkDirection {
  PLAY_TO_SERVER(EnvType.CLIENT, ServerboundCustomPayloadPacket.class, 1, (d, n) -> new ServerboundCustomPayloadPacket(n, d)),
  PLAY_TO_CLIENT(EnvType.SERVER, ClientboundCustomPayloadPacket.class, 0, (d, n) -> new ClientboundCustomPayloadPacket(n, d));

  private final EnvType logicalSide;
  private final Class<? extends Packet> packetClass;
  private final int otherWay;
  private final Factory factory;

  private static final Reference2ReferenceArrayMap<Class<? extends Packet>, NetworkDirection> packetLookup;

  static {
    packetLookup = Stream.of(values()).
      collect(Collectors.toMap(NetworkDirection::getPacketClass, Function.identity(), (m1, m2) -> m1, Reference2ReferenceArrayMap::new));
  }

  NetworkDirection(EnvType logicalSide, Class<? extends Packet> clazz, int i, Factory factory) {
    this.logicalSide = logicalSide;
    this.packetClass = clazz;
    this.otherWay = i;
    this.factory = factory;
  }

  private Class<? extends Packet> getPacketClass() {
    return packetClass;
  }

  public static <T extends Packet<?>> NetworkDirection directionFor(Class<T> customPacket) {
    return packetLookup.get(customPacket);
  }

  public NetworkDirection reply() {
    return NetworkDirection.values()[this.otherWay];
  }

  public EnvType getOriginationSide() {
    return logicalSide;
  }

  public EnvType getReceptionSide() {return reply().logicalSide;}

  @SuppressWarnings("unchecked")
  public Packet<?> buildPacket(FriendlyByteBuf buf, ResourceLocation channelName) {
    return this.factory.create(buf, channelName);
  }

  private interface Factory<T extends Packet<?>> {

    T create(FriendlyByteBuf data, ResourceLocation channelName);
  }
}
