package slimeknights.mantle.lib.mixin.common;

import slimeknights.mantle.lib.item.EntityTickListenerItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin {
	@Inject(method = "tick", at = @At("HEAD"), cancellable = true)
	public void create$onHeadTick(CallbackInfo ci) {
		ItemEntity self = (ItemEntity) (Object) this;
		ItemStack stack = self.getItem();
		if (stack.getItem() instanceof EntityTickListenerItem && ((EntityTickListenerItem) stack.getItem()).onEntityItemUpdate(stack, self)) {
			ci.cancel();
		}
	}
}