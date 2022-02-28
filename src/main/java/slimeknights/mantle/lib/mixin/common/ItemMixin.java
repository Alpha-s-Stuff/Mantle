package slimeknights.mantle.lib.mixin.common;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import slimeknights.mantle.lib.extensions.ItemExtensions;
import slimeknights.mantle.lib.extensions.RegistryNameProvider;
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
public abstract class ItemMixin implements ItemExtensions, RegistryNameProvider {
	@Unique
	private Supplier<Item> mantle$supplier;

	@Unique
	@Override
	public Supplier<Item> mantle$getSupplier() {
		return mantle$supplier;
	}

	@Inject(method = "<init>", at = @At("TAIL"))
	public void mantle$item(Item.Properties properties, CallbackInfo ci) {
		mantle$supplier = new ItemSupplier(MixinHelper.cast(this));
	}

  @Override
  public ResourceLocation getRegistryName() {
    return Registry.ITEM.getKey(MixinHelper.cast(this));
  }
}
