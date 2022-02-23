package slimeknights.mantle.lib.util;

import slimeknights.mantle.lib.mixin.accessor.DamageSourceAccessor;

import net.minecraft.world.damagesource.DamageSource;

public final class DamageSourceHelper {
	public static DamageSource mantle$createDamageSource(String string) {
		return DamageSourceAccessor.mantle$init(string);
	}

	// this is probably going to crash and burn.
	public static DamageSource mantle$createArmorBypassingDamageSource(String string) {
		return MixinHelper.<DamageSourceAccessor>cast(mantle$createDamageSource(string)).mantle$setDamageBypassesArmor();
	}

	public static DamageSource mantle$createFireDamageSource(String string) {
		return MixinHelper.<DamageSourceAccessor>cast(mantle$createDamageSource(string)).mantle$setFireDamage();
	}

	private DamageSourceHelper() {}
}
