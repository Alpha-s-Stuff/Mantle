package slimeknights.mantle.inventory;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;

import java.util.Iterator;

/**
 * Item handler that contains no items. Use similarly to {@link slimeknights.mantle.lib.transfer.fluid.EmptyFluidHandler}
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EmptyItemHandler implements Storage<ItemVariant> {
  public static final EmptyItemHandler INSTANCE = new EmptyItemHandler();

  @Override
  public long insert(ItemVariant resource, long maxAmount, TransactionContext transaction) {
    return 0;
  }

  @Override
  public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
    return 0;
  }

  @Override
  public Iterator<? extends StorageView<ItemVariant>> iterator(TransactionContext transaction) {
    return null;
  }
}
