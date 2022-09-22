package slimeknights.mantle.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import slimeknights.mantle.fabric.event.ClientRightClickAir;

@Mixin(Minecraft.class)
public class MinecraftMixin {

  @Shadow
  @Nullable
  public HitResult hitResult;

  @Shadow
  @Nullable
  public LocalPlayer player;

  // Taken from arch
  @Inject(method = "startUseItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isEmpty()Z", ordinal = 1),
    locals = LocalCapture.CAPTURE_FAILHARD)
  private void rightClickAir(CallbackInfo ci, InteractionHand[] var1, int var2, int var3, InteractionHand interactionHand, ItemStack itemStack) {
    if (itemStack.isEmpty() && (this.hitResult == null || this.hitResult.getType() == HitResult.Type.MISS)) {
      ClientRightClickAir.EVENT.invoker().click(player, interactionHand);
    }
  }
}
