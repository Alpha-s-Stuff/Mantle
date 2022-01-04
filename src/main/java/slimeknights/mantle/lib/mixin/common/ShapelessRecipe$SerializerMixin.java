package slimeknights.mantle.lib.mixin.common;

import com.simibubi.create.lib.util.Constants;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import net.minecraft.world.item.crafting.ShapelessRecipe;

@Mixin(ShapelessRecipe.Serializer.class)
public abstract class ShapelessRecipe$SerializerMixin {
	@ModifyConstant(
			method = "fromJson",
			constant = @Constant(intValue = 9)
	)
	private static int modifyMaxItemsInRecipe(int original) {
		return Constants.Crafting.HEIGHT * Constants.Crafting.WIDTH;
	}
}
