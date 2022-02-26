package slimeknights.mantle.lib.util;

import net.minecraft.world.entity.Entity;
import slimeknights.mantle.lib.mixin.accessor.EntityAccessor;

public final class EntityHelper {
	public static final String EXTRA_DATA_KEY = "create_ExtraEntityData";

	public static String getEntityString(Entity entity) {
		return ((EntityAccessor) entity).mantle$getEntityString();
	}

	private EntityHelper() {}
}
