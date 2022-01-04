package slimeknights.mantle.lib.mixin;

import com.google.gson.JsonObject;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;
import java.util.stream.Stream;

import net.minecraft.world.item.crafting.Ingredient;

@Mixin(Ingredient.class)
public interface IngredientAccessor {
    @Invoker
    static Ingredient callFromValues(Stream<? extends Ingredient.Value> stream) {
        throw new UnsupportedOperationException();
    }

    @Invoker
    static Ingredient.Value callValueFromJson(JsonObject json) {
        throw new UnsupportedOperationException();
    }

    @Accessor
    Ingredient.Value[] getValues();
}
