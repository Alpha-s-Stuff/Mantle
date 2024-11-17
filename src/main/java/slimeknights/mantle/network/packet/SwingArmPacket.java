package slimeknights.mantle.network.packet;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import slimeknights.mantle.Mantle;
import slimeknights.mantle.util.OffhandCooldownTracker;

/** Packet to tell a client to swing an entity arm, as the vanilla one resets cooldown */
public record SwingArmPacket(int entityId, InteractionHand hand) implements IThreadsafePacket {
  public static final Type<SwingArmPacket> TYPE = new Type<>(Mantle.getResource("swing_arm"));
  public static final StreamCodec<RegistryFriendlyByteBuf, SwingArmPacket> CODEC = CustomPacketPayload.codec(SwingArmPacket::encode, SwingArmPacket::new);

  public SwingArmPacket(Entity entity, InteractionHand hand) {
    this(entity.getId(), hand);
  }

  public SwingArmPacket(RegistryFriendlyByteBuf buffer) {
    this(buffer.readVarInt(), buffer.readEnum(InteractionHand.class));
  }

  public void encode(FriendlyByteBuf buffer) {
    buffer.writeVarInt(entityId);
    buffer.writeEnum(hand);
  }

  @Override
  public void handleThreadsafe(Context context) {
    HandleClient.handle(this);
  }

  @Override
  public Type<SwingArmPacket> type() {
    return TYPE;
  }

  private static class HandleClient {
    private static void handle(SwingArmPacket packet) {
      Level world = Minecraft.getInstance().level;
      if (world != null) {
        Entity entity = world.getEntity(packet.entityId);
        if (entity instanceof LivingEntity) {
          OffhandCooldownTracker.swingHand((LivingEntity) entity, packet.hand, false);
        }
      }
    }
  }
}
