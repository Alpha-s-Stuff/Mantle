package slimeknights.mantle.recipe.helper;

import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;

/**
 * This class simply exists because every recipe serializer has to extend the forge registry entry and implement the interface. Easier to just extend this class
 * @param <T> Recipe type
 */
public abstract class AbstractRecipeSerializer<T extends Recipe<?>> implements RecipeSerializer<T> {}
