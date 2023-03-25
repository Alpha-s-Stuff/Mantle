package slimeknights.mantle.transfer.item;

import io.github.fabricators_of_create.porting_lib.extensions.extensions.INBTSerializable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;

import java.util.Arrays;

public class ItemStackHandler implements IItemHandlerModifiable, INBTSerializable<CompoundTag> {
	protected ItemStack[] stacks;

	public ItemStackHandler(int stacks) {
		this.stacks = new ItemStack[stacks];
		Arrays.fill(this.stacks, ItemStack.EMPTY);
	}

	public ItemStackHandler() {
		this(1);
	}

	public void setSize(int size) {
		stacks = new ItemStack[size];
		Arrays.fill(stacks, ItemStack.EMPTY);
	}

	protected void onContentsChanged(int slot) {}

	// IItemHandler

	@Override
	public int getSlots() {
		return stacks.length;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return stacks[slot];
	}

	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean sim) {
		if (stack.isEmpty()) return ItemStack.EMPTY;
		if (!isItemValid(slot, stack)) return stack;

		ItemStack oldStack = getStackInSlot(slot);
		int limit = Math.min(getSlotLimit(slot), stack.getMaxStackSize());

		if (!oldStack.isEmpty()) {
			if (!slimeknights.mantle.transfer.item.ItemHandlerHelper.canItemStacksStack(stack, oldStack)) return stack;
			limit -= oldStack.getCount();
		}
		if (limit <= 0) return stack;

		boolean limitReached = stack.getCount() > limit;

		if (!sim) {
			if (oldStack.isEmpty()) {
				setStackInSlot(slot, limitReached ? slimeknights.mantle.transfer.item.ItemHandlerHelper.copyStackWithSize(stack, limit) : stack);
			} else {
				oldStack.grow(limitReached ? limit : stack.getCount());
			}
			onContentsChanged(slot);
		}
		return limitReached ? slimeknights.mantle.transfer.item.ItemHandlerHelper.copyStackWithSize(stack, stack.getCount() - limit) : ItemStack.EMPTY;
	}

	@Override
	public ItemStack extractItem(int slot, int amount, boolean sim) {
		if (amount == 0) return ItemStack.EMPTY;

		ItemStack oldStack = getStackInSlot(slot);
		if (oldStack.isEmpty()) return ItemStack.EMPTY;

		int toExtract = Math.min(amount, oldStack.getMaxStackSize());

		if (oldStack.getCount() <= toExtract) {
			if (!sim) {
				setStackInSlot(slot, ItemStack.EMPTY);
				onContentsChanged(slot);
				return oldStack;
			} else {
				return oldStack.copy();
			}
		} else {
			if (!sim) {
				setStackInSlot(slot, slimeknights.mantle.transfer.item.ItemHandlerHelper.copyStackWithSize(oldStack, oldStack.getCount() - toExtract));
				onContentsChanged(slot);
			}

			return ItemHandlerHelper.copyStackWithSize(oldStack, toExtract);
		}
	}

	@Override
	public int getSlotLimit(int slot) {
		return 64;
	}

	@Override
	public boolean isItemValid(int slot, ItemStack stack) {
		return true;
	}

	// Modifiable

	@Override
	public void setStackInSlot(int slot, ItemStack stack) {
		stacks[slot] = stack;
		onContentsChanged(slot);
	}

	// NBTSerializable

	public CompoundTag serializeNBT() {
		ListTag nbtTagList = new ListTag();
		for (int i = 0; i < stacks.length; i++) {
			if (!stacks[i].isEmpty()) {
				CompoundTag itemTag = new CompoundTag();
				itemTag.putInt("Slot", i);
				stacks[i].save(itemTag);
				nbtTagList.add(itemTag);
			}
		}
		CompoundTag nbt = new CompoundTag();
		nbt.put("Items", nbtTagList);
		nbt.putInt("Size", stacks.length);
		return nbt;
	}

	public void deserializeNBT(CompoundTag tag) {
		setSize(tag.contains("Size", Tag.TAG_INT) ? tag.getInt("Size") : stacks.length);
		ListTag tagList = tag.getList("Items", Tag.TAG_COMPOUND);
		for (int i = 0; i < tagList.size(); i++) {
			CompoundTag itemTags = tagList.getCompound(i);
			int slot = itemTags.getInt("Slot");

			if (slot >= 0 && slot < stacks.length) {
				stacks[slot] = ItemStack.of(itemTags);
			}
		}
		onLoad();
	}

	protected void onLoad() {
	}
}
