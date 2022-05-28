package slimeknights.mantle.inventory;

import io.github.fabricators_of_create.porting_lib.transfer.fluid.EmptyFluidHandler;
import io.github.fabricators_of_create.porting_lib.transfer.item.IItemHandler;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;

/**
 * Item handler that contains no items. Use similarly to {@link EmptyFluidHandler}
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EmptyItemHandler implements IItemHandler {
  public static final EmptyItemHandler INSTANCE = new EmptyItemHandler();

  @Override
  public int getSlots() {
    return 0;
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
  public boolean isItemValid(int slot, ItemStack stack) {
    return false;
  }

  @Nonnull
  @Override
  public ItemStack insertItem(int slot, ItemStack stack, TransactionContext t) {
    return stack;
  }

  @Nonnull
  @Override
  public ItemStack extractItem(int slot, int amount, TransactionContext t) {
    return ItemStack.EMPTY;
  }
}
