package slimeknights.mantle.mixin.client.rei;

import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.common.display.Display;
import net.minecraft.tags.TagKey;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Function;

@Mixin(value = DisplayRegistry.class, remap = false)
public interface DisplayRegistryMixin {
  @Inject(method = "registerFiller(Ljava/lang/Class;Ljava/util/function/Function;)V", at = @At("HEAD"), cancellable = true)
  default <T, D extends Display> void  mantle$disableTags(Class<T> typeClass, Function<? extends T, @Nullable D> filler, CallbackInfo ci) {
    if (typeClass == TagKey.class)
      ci.cancel();
  }
}
