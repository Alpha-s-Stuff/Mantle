package slimeknights.mantle.lib.mixin.common;

import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import slimeknights.mantle.lib.event.LivingEntityEvents;

@Mixin(AbstractHorse.class)
public class AbstractHorseMixin {
  @Inject(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/horse/AbstractHorse;setIsJumping(Z)V", shift = At.Shift.AFTER))
  public void mantle$horseJump(Vec3 travelVector, CallbackInfo ci) {
    LivingEntityEvents.JUMP.invoker().onLivingEntityJump((AbstractHorse) (Object) this);
  }
}
