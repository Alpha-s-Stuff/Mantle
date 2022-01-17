package slimeknights.mantle.lib.mixin.common;

import slimeknights.mantle.lib.extensions.ItemExtensions;
import slimeknights.mantle.lib.util.ItemSupplier;
import slimeknights.mantle.lib.util.MixinHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.function.Supplier;

import net.minecraft.world.item.Item;

@Mixin(Item.class)
public abstract class ItemMixin implements ItemExtensions {
	@Unique
	private Supplier<Item> create$supplier;

	@Unique
	@Override
	public Supplier<Item> create$getSupplier() {
		return create$supplier;
	}

	@Inject(method = "<init>", at = @At("TAIL"))
	public void create$item(Item.Properties properties, CallbackInfo ci) {
		create$supplier = new ItemSupplier(MixinHelper.cast(this));
	}
}
