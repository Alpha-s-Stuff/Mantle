package slimeknights.mantle.recipe;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.world.item.crafting.RecipeSerializer;

/**
 * All recipe serializers registered under Mantles name.
 * TODO: merge with other mantle registry classes?
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MantleRecipeSerializers {
  public static RecipeSerializer<?> CRAFTING_SHAPED_FALLBACK;
  public static RecipeSerializer<?> CRAFTING_SHAPED_RETEXTURED;
}
