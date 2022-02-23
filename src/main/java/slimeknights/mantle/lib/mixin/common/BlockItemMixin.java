package slimeknights.mantle.lib.mixin.common;

import slimeknights.mantle.lib.event.BlockPlaceCallback;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;

@Mixin(BlockItem.class)
public abstract class BlockItemMixin {
	@Inject(method = "useOn", at = @At("HEAD"), cancellable = true)
	private void mantle$useOn(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir) {
		InteractionResult result = BlockPlaceCallback.EVENT.invoker().onBlockPlace(new BlockPlaceContext(context));
		if (result != InteractionResult.PASS) {
			cir.setReturnValue(result);
		}
	}
}
