package slimeknights.mantle.lib.mixin.accessor;

import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.world.entity.LivingEntity;

@Mixin(LivingEntity.class)
public interface LivingEntityAccessor {
	@Accessor("jumping")
	boolean mantle$isJumping();

  @Accessor("lastPos")
  BlockPos mantle$lastPos();
}
