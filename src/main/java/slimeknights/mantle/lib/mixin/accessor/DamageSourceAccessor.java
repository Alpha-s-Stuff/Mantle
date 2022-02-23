package slimeknights.mantle.lib.mixin.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.world.damagesource.DamageSource;

@Mixin(DamageSource.class)
public interface DamageSourceAccessor {
	@Invoker("<init>")
	static DamageSource mantle$init(String string) {
		throw new AssertionError();
	}

	@Invoker("setIsFire")
	DamageSource mantle$setFireDamage();

	@Invoker("bypassArmor")
	DamageSource mantle$setDamageBypassesArmor();
}
