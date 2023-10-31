package slimeknights.mantle.inventory;

import io.github.fabricators_of_create.porting_lib.transfer.item.SlottedStackStorage;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;

/**
 * Item handler that contains no items. Use similarly to {@link slimeknights.mantle.lib.transfer.fluid.EmptyFluidHandler}
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EmptyItemHandler implements SlottedStackStorage {
  public static final EmptyItemHandler INSTANCE = new EmptyItemHandler();

  @Override
  public int getSlotCount() {
    return 0;
  }

  @Override
  public SingleSlotStorage<ItemVariant> getSlot(int slot) {
    return EmptyItemSlot.INSTANCE;
  }

  @Override
  public int getSlotLimit(int slot) {
    return 0;
  }

  @Nonnull
  @Override
  public ItemStack getStackInSlot(int slot) {
    return ItemStack.EMPTY;
  }

  @Override
  public void setStackInSlot(int slot, ItemStack stack) {}

  @Override
  public boolean isItemValid(int slot, ItemVariant variant, int count) {
    return false;
  }

  @Override
  public long insert(ItemVariant resource, long maxAmount, TransactionContext transaction) {
    return 0;
  }

  @Override
  public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
    return 0;
  }
}
