package slimeknights.mantle.lib.mixin.accessor;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;

@Environment(EnvType.CLIENT)
@Mixin(GameRenderer.class)
public interface GameRendererAccessor {
	@Invoker("getFov")
	double mantle$getFOVModifier(Camera camera, float tickDelta, boolean changingFov);
}
