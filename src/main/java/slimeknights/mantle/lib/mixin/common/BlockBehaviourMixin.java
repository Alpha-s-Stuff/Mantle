package slimeknights.mantle.lib.mixin.common;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import slimeknights.mantle.lib.event.PlayerBreakSpeedCallback;

@Mixin(BlockBehaviour.class)
public class BlockBehaviourMixin {
  @Inject(method = "getDestroyProgress", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;getDestroySpeed(Lnet/minecraft/world/level/block/state/BlockState;)F", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
  public void getDestroySpeed(BlockState state, Player player, BlockGetter level, BlockPos pos, CallbackInfoReturnable<Float> cir, float f) {
    float original = player.getDestroySpeed(state);
    PlayerBreakSpeedCallback.BreakSpeed speed = new PlayerBreakSpeedCallback.BreakSpeed(player, state, original, pos);
    PlayerBreakSpeedCallback.EVENT.invoker().setBreakSpeed(speed);
    float newSpeed = speed.getNewSpeed();
    if(newSpeed != original) {
      if (f == -1.0F) {
        cir.setReturnValue(0.0F);
      } else {
        int i = player.hasCorrectToolForDrops(state) ? 30 : 100;
        cir.setReturnValue(newSpeed / f / (float)i);
      }
    }
  }
}
