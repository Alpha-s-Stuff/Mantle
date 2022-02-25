package slimeknights.mantle.lib.mixin.client;

import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.item.ItemColors;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import slimeknights.mantle.lib.event.ColorHandlersCallback;

@Mixin(ItemColors.class)
public class ItemColorsMixin {
  @Inject(method = "createDefault", at = @At("TAIL"), locals = LocalCapture.CAPTURE_FAILHARD)
  private static void registerModdedColorHandlers(BlockColors colors, CallbackInfoReturnable<ItemColors> cir, ItemColors itemcolors) {
    ColorHandlersCallback.ITEM.invoker().registerItemColors(itemcolors, colors);
  }
}
