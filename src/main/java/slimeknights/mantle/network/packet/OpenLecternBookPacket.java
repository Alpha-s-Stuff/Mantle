package slimeknights.mantle.network.packet;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import slimeknights.mantle.Mantle;
import slimeknights.mantle.item.ILecternBookItem;

/**
 * Packet to open a book on a lectern
 */
public record OpenLecternBookPacket(BlockPos pos, ItemStack book) implements IThreadsafePacket {
  public static final Type<OpenLecternBookPacket> TYPE = new Type<>(Mantle.getResource("open_lectern"));
  public static final StreamCodec<RegistryFriendlyByteBuf, OpenLecternBookPacket> CODEC = StreamCodec.composite(BlockPos.STREAM_CODEC, OpenLecternBookPacket::pos, ItemStack.STREAM_CODEC, OpenLecternBookPacket::book, OpenLecternBookPacket::new);

  @Override
  public void handleThreadsafe(Context context) {
    if (book.getItem() instanceof ILecternBookItem) {
      ((ILecternBookItem)book.getItem()).openLecternScreenClient(pos, book);
    }
  }

  @Override
  public Type<OpenLecternBookPacket> type() {
    return TYPE;
  }
}
