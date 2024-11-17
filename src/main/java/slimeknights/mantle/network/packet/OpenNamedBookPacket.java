package slimeknights.mantle.network.packet;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import slimeknights.mantle.Mantle;
import slimeknights.mantle.client.book.BookLoader;
import slimeknights.mantle.client.book.data.BookData;

public record OpenNamedBookPacket(ResourceLocation book) implements IThreadsafePacket {
  public static final Type<OpenNamedBookPacket> TYPE = new Type<>(Mantle.getResource("open_named_book"));
  public static final StreamCodec<RegistryFriendlyByteBuf, OpenNamedBookPacket> CODEC = StreamCodec.composite(ResourceLocation.STREAM_CODEC, OpenNamedBookPacket::book, OpenNamedBookPacket::new);
  private static final String BOOK_ERROR = "command.mantle.book_test.not_found";

  @Override
  public void handleThreadsafe(ISimplePacket.Context context) {
    BookData bookData = BookLoader.getBook(book);
    if(bookData != null) {
      bookData.openGui(Component.literal("Book"), "", null, null);
    } else {
      ClientOnly.errorStatus(book);
    }
  }

  @Override
  public Type<OpenNamedBookPacket> type() {
    return TYPE;
  }

  static class ClientOnly {
    static void errorStatus(ResourceLocation book) {
      Player player = Minecraft.getInstance().player;
      if (player != null) {
        player.displayClientMessage(Component.translatable(BOOK_ERROR, book).withStyle(ChatFormatting.RED), false);
      }
    }
  }
}
