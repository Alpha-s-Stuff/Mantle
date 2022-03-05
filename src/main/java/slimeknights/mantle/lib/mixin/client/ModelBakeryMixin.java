package slimeknights.mantle.lib.mixin.client;

import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import slimeknights.mantle.lib.event.ModelLoadCallback;

@Mixin(ModelBakery.class)
public class ModelBakeryMixin {
  @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiling/ProfilerFiller;push(Ljava/lang/String;)V"))
  public void onModelLoad(ResourceManager resourceManager, BlockColors blockColors, ProfilerFiller profilerFiller, int i, CallbackInfo ci) {
    ModelLoadCallback.EVENT.invoker().onModelsStartLoading();
  }
}
