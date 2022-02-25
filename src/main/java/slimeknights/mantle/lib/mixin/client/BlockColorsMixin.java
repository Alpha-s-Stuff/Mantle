package slimeknights.mantle.lib.mixin.client;

import net.minecraft.client.color.block.BlockColors;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import slimeknights.mantle.lib.event.ColorHandlersCallback;

@Mixin(BlockColors.class)
public class BlockColorsMixin {
  @Inject(method = "createDefault", at = @At("TAIL"), locals = LocalCapture.CAPTURE_FAILHARD)
  private static void registerModdedColorHandlers(CallbackInfoReturnable<BlockColors> cir, BlockColors blockColors) {
    ColorHandlersCallback.BLOCK.invoker().registerBlockColors(blockColors);
  }
}
