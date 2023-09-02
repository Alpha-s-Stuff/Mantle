package slimeknights.mantle.inventory;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.item.base.SingleStackStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import slimeknights.mantle.block.entity.MantleBlockEntity;
import slimeknights.mantle.transfer.item.IItemHandlerModifiable;
import slimeknights.mantle.transfer.item.ItemHandlerHelper;

import javax.annotation.Nonnull;

/**
 * Item handler containing exactly one item.
 */
@SuppressWarnings("unused")
@RequiredArgsConstructor
public abstract class SingleItemHandler<T extends MantleBlockEntity> extends SingleStackStorage {
  protected final T parent;
  private final int maxStackSize;

  /** Current item in this slot */
  @Getter
  private ItemStack stack = ItemStack.EMPTY;

  /**
   * Sets the stack in this duct
   * @param newStack  New stack
   */
  public void setStack(ItemStack newStack) {
    this.stack = newStack;
    parent.setChangedFast();
  }

  /**
   * Checks if the given stack is valid for this slot
   * @param stack  Stack
   * @return  True if valid
   */
  protected abstract boolean isItemValid(ItemVariant stack);


  /* Properties */

  @Override
  public boolean canInsert(ItemVariant stack) {
    return isItemValid(stack);
  }

  @Override
  protected boolean canExtract(ItemVariant itemVariant) {
    return isItemValid(itemVariant);
  }

  @Override
  public int getSlotCount() {
    return 1;
  }

  @Nonnull
  @Override
  public ItemStack getStack() {
    return stack;
  }


  /* Interaction */

  /**
   * Writes this module to NBT
   * @return  Module in NBT
   */
  public CompoundTag writeToNBT() {
    CompoundTag nbt = new CompoundTag();
    if (!stack.isEmpty()) {
      stack.save(nbt);
    }
    return nbt;
  }

  /**
   * Reads this module from NBT
   * @param nbt  NBT
   */
  public void readFromNBT(CompoundTag nbt) {
    stack = ItemStack.of(nbt);
  }
}
