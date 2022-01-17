package slimeknights.mantle.lib.util;

import slimeknights.mantle.lib.extensions.EntityExtensions;
import slimeknights.mantle.lib.mixin.accessor.EntityAccessor;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;

public final class EntityHelper {
	public static final String EXTRA_DATA_KEY = "create_ExtraEntityData";

	public static CompoundTag getExtraCustomData(Entity entity) {
		return ((EntityExtensions) entity).create$getExtraCustomData();
	}

	public static String getEntityString(Entity entity) {
		return ((EntityAccessor) entity).create$getEntityString();
	}

	private EntityHelper() {}
}
