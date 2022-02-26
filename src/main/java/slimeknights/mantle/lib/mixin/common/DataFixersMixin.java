package slimeknights.mantle.lib.mixin.common;

import com.mojang.datafixers.DataFixerBuilder;
import net.minecraft.util.datafix.DataFixers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import slimeknights.mantle.lib.event.RegisterDataFixerCallback;

@Mixin(DataFixers.class)
public class DataFixersMixin {
  @Inject(method = "addFixers", at = @At("TAIL"))
  private static void registerDataFixers(DataFixerBuilder builder, CallbackInfo ci) {
    RegisterDataFixerCallback.EVENT.invoker().addDataFixers(builder);
  }
}
