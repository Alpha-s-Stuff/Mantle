package slimeknights.mantle.lib.util;

import slimeknights.mantle.lib.mixin.accessor.LivingEntityRendererAccessor;

import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;

public class LivingRendererHelper {
	public static boolean addRenderer(LivingEntityRenderer renderer, RenderLayer toAdd) {
		return ((LivingEntityRendererAccessor) renderer).mantle$addLayer(toAdd);
	}
}
