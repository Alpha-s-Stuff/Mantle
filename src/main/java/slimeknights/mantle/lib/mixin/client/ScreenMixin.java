package slimeknights.mantle.lib.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import slimeknights.mantle.lib.event.RenderTooltipBorderColorCallback;
import slimeknights.mantle.lib.util.ScreenHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.List;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemStack;

@Environment(EnvType.CLIENT)
@Mixin(Screen.class)
public abstract class ScreenMixin {
	@Unique
	private ItemStack mantle$cachedStack = ItemStack.EMPTY;

	@Inject(method = "renderTooltip(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/world/item/ItemStack;II)V", at = @At("HEAD"))
	private void mantle$cacheItemStack(PoseStack matrixStack, ItemStack itemStack, int i, int j, CallbackInfo ci) {
		mantle$cachedStack = itemStack;
	}

	@Inject(method = "renderTooltip(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/world/item/ItemStack;II)V", at = @At("RETURN"))
	private void mantle$clearCachedItemStack(PoseStack matrixStack, ItemStack itemStack, int i, int j, CallbackInfo ci) {
		mantle$cachedStack = ItemStack.EMPTY;
	}

	@Inject(method = "renderTooltipInternal", at = @At("HEAD"))
	private void mantle$cacheBorderColors(PoseStack matrixStack, List<? extends FormattedCharSequence> list, int i, int j, CallbackInfo ci) {
		ScreenHelper.CURRENT_COLOR = RenderTooltipBorderColorCallback.EVENT.invoker()
				.onTooltipBorderColor(mantle$cachedStack, ScreenHelper.DEFAULT_BORDER_COLOR_START, ScreenHelper.DEFAULT_BORDER_COLOR_END);
	}

	@Inject(method = "renderTooltipInternal", at = @At("RETURN"))
	private void mantle$clearBorderColors(PoseStack matrixStack, List<? extends FormattedCharSequence> list, int i, int j, CallbackInfo ci) {
		ScreenHelper.CURRENT_COLOR = null;
	}
}
