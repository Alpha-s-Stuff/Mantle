package slimeknights.mantle.lib.mixin.common;

import com.google.gson.JsonArray;
import slimeknights.mantle.lib.util.Constants;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.world.item.crafting.ShapedRecipe;

@Mixin(ShapedRecipe.class)
public abstract class ShapedRecipeMixin {
	@ModifyConstant(
			method = "patternFromJson(Lcom/google/gson/JsonArray;)[Ljava/lang/String;",
			constant = @Constant(intValue = 3, ordinal = 0)
	)
	private static int mantle$modifyMaxHeight(int original) {
		return Constants.Crafting.HEIGHT;
	}

	@ModifyConstant(
			method = "patternFromJson(Lcom/google/gson/JsonArray;)[Ljava/lang/String;",
			constant = @Constant(intValue = 3, ordinal = 1)
	)
	private static int mantle$modifyMaxWidth(int original) {
		return Constants.Crafting.WIDTH;
	}
}
