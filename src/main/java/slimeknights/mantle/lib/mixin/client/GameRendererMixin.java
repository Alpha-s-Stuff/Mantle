package slimeknights.mantle.lib.mixin.client;

import com.mojang.blaze3d.shaders.Program;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.server.packs.resources.ResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import slimeknights.mantle.lib.event.RegisterShadersCallback;

import java.util.ArrayList;
import java.util.List;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
  @Inject(method = "reloadShaders", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z", ordinal = 0), locals = LocalCapture.CAPTURE_FAILSOFT)
  public void onShaderReload(ResourceManager manager, CallbackInfo ci, List<Program> list, ArrayList list2) {
    RegisterShadersCallback.EVENT.invoker().onShaderReload(manager, new RegisterShadersCallback.ShaderRegistry(list2));
  }
}
