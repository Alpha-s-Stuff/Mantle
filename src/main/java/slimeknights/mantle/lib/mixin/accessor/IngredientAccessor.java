package slimeknights.mantle.lib.mixin.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;
import java.util.stream.Stream;

import net.minecraft.world.item.crafting.Ingredient;

@Mixin(Ingredient.class)
public interface IngredientAccessor {
	@Accessor("values")
	Ingredient.Value[] mantle$getAcceptedItems();

	@Invoker("fromValues")
	static Ingredient mantle$fromValues(Stream<? extends Ingredient.Value> stream) {
		throw new AssertionError("Mixin application failed!");
	}
}
