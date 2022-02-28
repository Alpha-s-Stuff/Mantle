package slimeknights.mantle.lib.mixin.common;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.monster.Spider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import slimeknights.mantle.lib.event.PotionEvents;
import slimeknights.mantle.lib.util.MixinHelper;

@Mixin(Spider.class)
public class SpiderMixin {
  @Inject(method = "canBeAffected", at = @At("HEAD"), cancellable = true)
  public void mantle$potionApplicable(MobEffectInstance potioneffect, CallbackInfoReturnable<Boolean> cir) {
    if(potioneffect.getEffect() == MobEffects.POISON) {
      PotionEvents.PotionApplicableEvent event = new PotionEvents.PotionApplicableEvent(MixinHelper.cast(this), potioneffect);
      event.sendEvent();
      if (event.getResult() != InteractionResult.PASS)
        cir.setReturnValue(event.getResult() == InteractionResult.SUCCESS);
    }
  }
}
