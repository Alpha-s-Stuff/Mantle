package slimeknights.mantle.recipe;

import io.github.fabricators_of_create.porting_lib.util.LazyRegistrar;
import io.github.fabricators_of_create.porting_lib.util.RegistryObject;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import slimeknights.mantle.Mantle;
import slimeknights.mantle.recipe.cooking.BlastingResultRecipe;
import slimeknights.mantle.recipe.cooking.CampfireResultRecipe;
import slimeknights.mantle.recipe.cooking.SmeltingResultRecipe;
import slimeknights.mantle.recipe.cooking.SmokingResultRecipe;
import slimeknights.mantle.recipe.crafting.ShapedFallbackRecipe;
import slimeknights.mantle.recipe.crafting.ShapedRetexturedRecipe;
import slimeknights.mantle.recipe.helper.LoadableRecipeSerializer;

/** Handles any custom recipes added by Mantle */
public class MantleRecipes {
  private static final LazyRegistrar<RecipeSerializer<?>> RECIPES = LazyRegistrar.create(Registries.RECIPE_SERIALIZER, Mantle.modId);

  private MantleRecipes() {}

  /** Registers this to the bus */
  public static void init() {
    RECIPES.register();
  }

  // crafting
  public static final RegistryObject<ShapedFallbackRecipe.Serializer> CRAFTING_SHAPED_FALLBACK = RECIPES.register("crafting_shaped_fallback", ShapedFallbackRecipe.Serializer::new);
  public static final RegistryObject<ShapedRetexturedRecipe.Serializer> CRAFTING_SHAPED_RETEXTURED = RECIPES.register("crafting_shaped_retextured", ShapedRetexturedRecipe.Serializer::new);
  // cooking
  public static final RegistryObject<RecipeSerializer<SmeltingResultRecipe>> SMELTING = RECIPES.register("smelting", () -> LoadableRecipeSerializer.of(SmeltingResultRecipe.LOADABLE));
  public static final RegistryObject<RecipeSerializer<BlastingResultRecipe>> BLASTING = RECIPES.register("blasting", () -> LoadableRecipeSerializer.of(BlastingResultRecipe.LOADABLE));
  public static final RegistryObject<RecipeSerializer<SmokingResultRecipe>> SMOKING = RECIPES.register("smoking", () -> LoadableRecipeSerializer.of(SmokingResultRecipe.LOADABLE));
  public static final RegistryObject<RecipeSerializer<CampfireResultRecipe>> CAMPFIRE = RECIPES.register("campfire", () -> LoadableRecipeSerializer.of(CampfireResultRecipe.LOADABLE));
}
