package slimeknights.mantle.lib.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import slimeknights.mantle.lib.mixin.accessor.EntityAccessor;
import slimeknights.mantle.lib.mixin.accessor.LivingEntityAccessor;

public final class EntityHelper {
	public static final String EXTRA_DATA_KEY = "create_ExtraEntityData";

	public static String getEntityString(Entity entity) {
		return ((EntityAccessor) entity).mantle$getEntityString();
	}

  public static BlockPos getLastPos(LivingEntity entity) {
    return ((LivingEntityAccessor) entity).mantle$lastPos();
  }

	private EntityHelper() {}
}
