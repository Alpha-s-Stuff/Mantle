package slimeknights.mantle.network.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import slimeknights.mantle.Mantle;
import slimeknights.mantle.client.book.BookHelper;

/**
 * Packet to update the page in a book in the players hand
 */
public record UpdateHeldPagePacket(InteractionHand hand, String page) implements IThreadsafePacket {
  public static final Type<UpdateHeldPagePacket> TYPE = new Type<>(Mantle.getResource("update_held_page"));
  public static final StreamCodec<RegistryFriendlyByteBuf, UpdateHeldPagePacket> CODEC = CustomPacketPayload.codec(UpdateHeldPagePacket::encode, UpdateHeldPagePacket::new);

  public UpdateHeldPagePacket(FriendlyByteBuf buffer) {
    this(buffer.readEnum(InteractionHand.class), buffer.readUtf(100));
  }

  public void encode(RegistryFriendlyByteBuf buf) {
    buf.writeEnum(hand);
    buf.writeUtf(this.page);
  }

  @Override
  public Type<UpdateHeldPagePacket> type() {
    return TYPE;
  }

  @Override
  public void handleThreadsafe(Context context) {
    Player player = context.getSender();
    if (player != null && this.page != null) {
      ItemStack stack = player.getItemInHand(hand);
      if (!stack.isEmpty()) {
        BookHelper.writeSavedPageToBook(stack, this.page);
      }
    }
  }
}
