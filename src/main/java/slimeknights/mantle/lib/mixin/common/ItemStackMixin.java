package slimeknights.mantle.lib.mixin.common;

import slimeknights.mantle.lib.item.CustomMaxCountItem;
import slimeknights.mantle.lib.util.MixinHelper;
import slimeknights.mantle.lib.util.NBTSerializable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements NBTSerializable {
	@Inject(method = "getMaxStackSize", at = @At("HEAD"), cancellable = true)
	public void mantle$onGetMaxCount(CallbackInfoReturnable<Integer> cir) {
		ItemStack self = (ItemStack) (Object) this;
		Item item = self.getItem();
		if (item instanceof CustomMaxCountItem) {
			cir.setReturnValue(((CustomMaxCountItem) item).getItemStackLimit(self));
		}
	}

	@Override
	public CompoundTag mantle$serializeNBT() {
		CompoundTag nbt = new CompoundTag();
		MixinHelper.<ItemStack>cast(this).save(nbt);
		return nbt;
	}

	@Override
	public void mantle$deserializeNBT(CompoundTag nbt) {
		MixinHelper.<ItemStack>cast(this).setTag(ItemStack.of(nbt).getTag());
	}
}
