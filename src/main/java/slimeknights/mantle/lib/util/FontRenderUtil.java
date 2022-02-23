package slimeknights.mantle.lib.util;

import slimeknights.mantle.lib.mixin.accessor.FontAccessor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.font.FontSet;
import net.minecraft.resources.ResourceLocation;

@Environment(EnvType.CLIENT)
public final class FontRenderUtil {

	public static FontSet getFontStorage(Font renderer, ResourceLocation location) {
		return get(renderer).mantle$getFontSet(location);
	}

	private static FontAccessor get(Font renderer) {
		return MixinHelper.cast(renderer);
	}

	private FontRenderUtil() {}
}
