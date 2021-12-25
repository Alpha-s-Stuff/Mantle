package slimeknights.mantle.client.book.data.content;

import net.minecraft.resources.ResourceLocation;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import slimeknights.mantle.Mantle;
import slimeknights.mantle.client.book.data.BookData;
import slimeknights.mantle.client.screen.book.element.BookElement;

import java.util.ArrayList;

@Environment(EnvType.CLIENT)
public class ContentBlank extends PageContent {
  public static final ResourceLocation ID = Mantle.getResource("blank");

  @Override
  public void build(BookData book, ArrayList<BookElement> list, boolean rightSide) {
  }
}
