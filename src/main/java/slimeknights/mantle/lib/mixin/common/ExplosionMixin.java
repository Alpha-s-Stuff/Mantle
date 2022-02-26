package slimeknights.mantle.lib.mixin.common;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import slimeknights.mantle.lib.event.ExplosionEvents;

import java.util.List;
import java.util.Set;

@Mixin(Explosion.class)
public class ExplosionMixin {

  @Shadow
  @Final
  private Level level;

  @Inject(method = "explode", at = @At(value = "NEW", target = "Lnet/minecraft/world/phys/Vec3;<init>(DDD)V", ordinal = 0), locals = LocalCapture.CAPTURE_FAILHARD)
  public void onExplode(CallbackInfo ci, Set set, float j, int k, int l, int d, int q, int e, int r, List<Entity> list) {
    ExplosionEvents.DETONATE.invoker().onDetonate(this.level, (Explosion) (Object) this, list, j);
  }
}
