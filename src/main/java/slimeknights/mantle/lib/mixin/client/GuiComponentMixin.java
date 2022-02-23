package slimeknights.mantle.lib.mixin.client;

import slimeknights.mantle.lib.util.ScreenHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import net.minecraft.client.gui.GuiComponent;

@Environment(EnvType.CLIENT)
@Mixin(GuiComponent.class)
public abstract class GuiComponentMixin {
	@ModifyVariable(
			method = "fillGradient(Lcom/mojang/math/Matrix4f;Lcom/mojang/blaze3d/vertex/BufferBuilder;IIIIIII)V",
			at = @At("HEAD"),
			ordinal = 5,
			argsOnly = true
	)
	private static int mantle$replaceA(int a) {
		return mantle$getColor(a);
	}

	@ModifyVariable(
			method = "fillGradient(Lcom/mojang/math/Matrix4f;Lcom/mojang/blaze3d/vertex/BufferBuilder;IIIIIII)V",
			at = @At("HEAD"),
			ordinal = 6,
			argsOnly = true
	)
	private static int mantle$replaceB(int b) {
		return mantle$getColor(b);
	}

	private static int mantle$getColor(int original) {
		if (ScreenHelper.CURRENT_COLOR != null) {
			if (original == ScreenHelper.DEFAULT_BORDER_COLOR_START) {
				return ScreenHelper.CURRENT_COLOR.getBorderColorStart();
			} else if (original == ScreenHelper.DEFAULT_BORDER_COLOR_END) {
				return ScreenHelper.CURRENT_COLOR.getBorderColorEnd();
			}
		}
		return original;
	}
}
