package slimeknights.mantle.lib.mixin.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.entity.player.Player;
import slimeknights.mantle.lib.event.OverlayRenderCallback;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import static net.minecraft.client.gui.GuiComponent.GUI_ICONS_LOCATION;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GameRenderer;

@Environment(EnvType.CLIENT)
@Mixin(Gui.class)
public abstract class GuiMixin {
	@Shadow
	@Final
	private Minecraft minecraft;
	@Unique
	public float partialTicks;

	@Inject(method = "render", at = @At("HEAD"))
	public void mantle$render(PoseStack matrixStack, float f, CallbackInfo ci) {
		partialTicks = f;
	}

	//This might be the wrong method to inject to
	@Inject(
			method = "renderPlayerHealth",
			at = @At(
					value = "INVOKE",
					shift = At.Shift.AFTER,
					target = "Lnet/minecraft/util/profiling/ProfilerFiller;pop()V"
			),
    cancellable = true
	)
	private void renderStatusBars(PoseStack matrixStack, CallbackInfo ci) {
		if(OverlayRenderCallback.EVENT.invoker().onOverlayRender(matrixStack, partialTicks, minecraft.getWindow(), OverlayRenderCallback.Types.AIR)) {
      ci.cancel();
    }
	}

  //This might be the wrong method to inject to
  @Inject(
    method = "renderHearts",
    at = @At(
      value = "HEAD"
    ),
    cancellable = true
  )
  private void renderHealth(PoseStack matrixStack, Player player, int i, int j, int k, int l, float f, int m, int n, int o, boolean bl, CallbackInfo ci) {
    if(OverlayRenderCallback.EVENT.invoker().onOverlayRender(matrixStack, partialTicks, minecraft.getWindow(), OverlayRenderCallback.Types.PLAYER_HEALTH)) {
      ci.cancel();
    }
  }

	@Inject(method = "renderCrosshair", at = @At("HEAD"))
	private void renderCrosshair(PoseStack matrixStack, CallbackInfo ci) {
		OverlayRenderCallback.EVENT.invoker().onOverlayRender(matrixStack, partialTicks, minecraft.getWindow(), OverlayRenderCallback.Types.CROSSHAIRS);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderTexture(0, GUI_ICONS_LOCATION);
		RenderSystem.enableBlend();
	}
}
