package slimeknights.mantle.client.book.data.element;

import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.util.ResourceLocation;
import slimeknights.mantle.client.book.repository.BookRepository;

public class DataLocation implements IDataElement {

  public String file;
  public transient ResourceLocation location;

  @Override
  public void load(BookRepository source) {
    this.location = "$BLOCK_ATLAS".equals(this.file) ? PlayerContainer.BLOCK_ATLAS : source.getResourceLocation(this.file, true);
  }
}
