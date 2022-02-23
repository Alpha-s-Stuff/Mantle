package slimeknights.mantle.lib.mixin.common;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import slimeknights.mantle.lib.event.ExplosionStartCallback;

@Mixin(ServerLevel.class)
public class ServerLevelMixin {
  @Inject(method = "explode", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Explosion;explode()V", shift = At.Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILHARD)
  @SuppressWarnings("ALL")
  public void mantle$onStartExplosion(Entity exploder, DamageSource damageSource, ExplosionDamageCalculator context, double x, double y, double z, float size, boolean causesFire, Explosion.BlockInteraction mode, CallbackInfoReturnable<Explosion> cir, Explosion explosion) {
    if(ExplosionStartCallback.EVENT.invoker().onExplosionStart((Level) (Object) this, explosion)) cir.setReturnValue(explosion);
  }
}
