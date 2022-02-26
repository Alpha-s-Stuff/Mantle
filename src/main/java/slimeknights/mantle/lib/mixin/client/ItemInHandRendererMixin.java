package slimeknights.mantle.lib.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import slimeknights.mantle.lib.event.RenderHandCallback;
import slimeknights.mantle.lib.extensions.ItemExtensions;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import javax.annotation.Nonnull;

import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

@Environment(EnvType.CLIENT)
@Mixin(ItemInHandRenderer.class)
public abstract class ItemInHandRendererMixin {
	@Shadow
	private ItemStack mainHandItem;
	@Shadow
	private ItemStack offHandItem;
	private static int mantle$mainHandSlot = 0;

	private static boolean mantle$shouldCauseReequipAnimation(@Nonnull ItemStack from, @Nonnull ItemStack to, int slot) {
		if (from.isEmpty() && to.isEmpty()) return false;
		if (from.isEmpty() || to.isEmpty()) return true;

		boolean changed = false;
		if (slot != -1) {
			changed = slot != mantle$mainHandSlot;
			mantle$mainHandSlot = slot;
		}
		return ((ItemExtensions)from.getItem()).shouldCauseReequipAnimation(from, to, changed);
	}

	@Inject(method = "renderArmWithItem", at = @At("HEAD"), cancellable = true)
	private void mantle$renderArmWithItem(AbstractClientPlayer player, float tickDelta, float pitch, InteractionHand hand, float swingProgress, ItemStack stack, float equipProgress, PoseStack matrices, MultiBufferSource vertexConsumers, int light, CallbackInfo ci) {
		RenderHandCallback.RenderHandEvent event = new RenderHandCallback.RenderHandEvent(player, hand, stack, matrices, vertexConsumers, tickDelta, pitch, swingProgress, equipProgress, light);
		RenderHandCallback.EVENT.invoker().onRenderHand(event);
		if (event.isCanceled()) {
			ci.cancel();
		}
	}

	@Inject(
			method = "tick",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/player/LocalPlayer;getAttackStrengthScale(F)F",
					shift = At.Shift.AFTER
			),
			locals = LocalCapture.CAPTURE_FAILHARD
	)
	public void mantle$tick(CallbackInfo ci,
							LocalPlayer clientPlayerEntity, ItemStack itemStack, ItemStack itemStack2) {
		if (mantle$shouldCauseReequipAnimation(mainHandItem, itemStack, clientPlayerEntity.getInventory().selected)) {
			mainHandItem = itemStack;
		}

		if (mantle$shouldCauseReequipAnimation(offHandItem, itemStack2, -1)) {
			offHandItem = itemStack2;
		}
	}
}
