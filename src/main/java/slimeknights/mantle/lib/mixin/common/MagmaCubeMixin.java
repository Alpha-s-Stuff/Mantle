package slimeknights.mantle.lib.mixin.common;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.MagmaCube;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import slimeknights.mantle.lib.event.LivingEntityEvents;

@Mixin(MagmaCube.class)
public class MagmaCubeMixin {
  @Inject(method = "jumpFromGround", at = @At("TAIL"))
  public void mantle$onMagmaJump(CallbackInfo ci) {
    LivingEntityEvents.JUMP.invoker().onLivingEntityJump((LivingEntity) (Object) this);
  }
}
