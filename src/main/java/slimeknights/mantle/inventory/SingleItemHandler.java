package slimeknights.mantle.inventory;

import io.github.fabricators_of_create.porting_lib.transfer.item.SlottedStackStorage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.item.base.SingleStackStorage;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import slimeknights.mantle.block.entity.MantleBlockEntity;

import javax.annotation.Nonnull;

/**
 * Item handler containing exactly one item.
 */
@SuppressWarnings("unused")
@RequiredArgsConstructor
public abstract class SingleItemHandler<T extends MantleBlockEntity> implements SlottedStackStorage {
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
  public boolean isItemValid(int slot, ItemVariant stack) {
    return slot == 0 && isItemValid(stack);
  }

  @Override
  public int getSlotCount() {
    return 1;
  }

  @Override
  public int getSlotLimit(int slot) {
    return maxStackSize;
  }

  @Nonnull
  @Override
  public ItemStack getStackInSlot(int slot) {
    if (slot == 0) {
      return stack;
    }
    return ItemStack.EMPTY;
  }


  /* Interaction */

  @Override
  public void setStackInSlot(int slot, ItemStack stack) {
    if (slot == 0) {
      setStack(stack);
    }
  }

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
