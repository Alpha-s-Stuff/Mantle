package slimeknights.mantle.transfer.item;

import javax.annotation.Nonnull;

import com.google.common.collect.Iterators;
import io.github.fabricators_of_create.porting_lib.transfer.TransferUtil;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class SlotItemHandlerFabric extends Slot {
	private static final Container emptyInventory = new SimpleContainer(0);
	private final Storage<ItemVariant> itemHandler;
	private final int index;

	public SlotItemHandlerFabric(Storage<ItemVariant> itemHandler, int index, int xPosition, int yPosition) {
		super(emptyInventory, index, xPosition, yPosition);
		this.itemHandler = itemHandler;
		this.index = index;
	}

	@Override
	public boolean mayPlace(@Nonnull ItemStack stack) {
		return !stack.isEmpty();
	}

	@Override
	@Nonnull
	public ItemStack getItem() {
    try (Transaction t = TransferUtil.getTransaction()) {
      if (Iterators.size(getItemHandler().iterator()) <= index) return ItemStack.EMPTY;
      StorageView<ItemVariant> view = Iterators.get(this.getItemHandler().iterator(), index);
      return view.getResource().toStack((int) view.getAmount());
    }
	}

	@Override
	public void set(@Nonnull ItemStack stack) {
    if (getItemHandler() instanceof InventoryStorage inventoryStorage) {
      SingleSlotStorage<ItemVariant> slot = inventoryStorage.getSlot(index);
      TransferUtil.clearStorage(slot);
      TransferUtil.insertItem(slot, stack);
    } else {
      try (Transaction t = TransferUtil.getTransaction()) {
        getItemHandler().insert(ItemVariant.of(stack), stack.getCount(), t);
        t.commit();
      }
    }
		this.setChanged();
	}

	@Override
	public void onQuickCraft(@Nonnull ItemStack oldStackIn, @Nonnull ItemStack newStackIn) {

	}

	@Override
	public int getMaxStackSize(@Nonnull ItemStack stack) {
		ItemStack maxAdd = stack.copy();
		int maxInput = stack.getMaxStackSize();
		maxAdd.setCount(maxInput);
    try (Transaction t = TransferUtil.getTransaction()) {
      Storage<ItemVariant> handler = this.getItemHandler();
      StorageView<ItemVariant> slot = Iterators.get(handler.iterator(), index);
      ItemStack currentStack = slot.getResource().toStack((int) slot.getAmount());
      if (handler instanceof InventoryStorage inventoryStorage) {
        SingleSlotStorage<ItemVariant> slotStorage = inventoryStorage.getSlot(index);
        TransferUtil.clearStorage(slotStorage);

        long remainder = 0;
        try (Transaction emulate = TransferUtil.getTransaction()) {
          remainder = TransferUtil.insertItem(inventoryStorage.getSlot(index), maxAdd);
        }
        TransferUtil.insertItem(slotStorage, currentStack);

        return (int) (maxInput - remainder);
      } else {
        try (Transaction emulate = TransferUtil.getTransaction()) {
          long remainder = TransferUtil.insertItem(handler, maxAdd);

          int current = currentStack.getCount();
          long added = maxInput - remainder;
          return (int) (current + added);
        }
      }
    }
	}

	@Override
	public boolean mayPickup(Player playerIn) {
    if (getItemHandler() instanceof InventoryStorage inventoryStorage)
      return !TransferUtil.extractAnyItem(inventoryStorage.getSlot(index), 1).isEmpty();
		return !TransferUtil.extractAnyItem(this.getItemHandler(), 1).isEmpty();
	}

	@Override
	@Nonnull
	public ItemStack remove(int amount) {
    if (getItemHandler() instanceof InventoryStorage inventoryStorage)
      return TransferUtil.extractAnyItem(inventoryStorage.getSlot(index), amount);
		return TransferUtil.extractAnyItem(this.getItemHandler(), amount);
	}

	public Storage<ItemVariant> getItemHandler() {
		return itemHandler;
	}
}
