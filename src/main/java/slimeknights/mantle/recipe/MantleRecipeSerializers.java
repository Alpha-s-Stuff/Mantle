package slimeknights.mantle.recipe;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.world.item.crafting.RecipeSerializer;
import slimeknights.mantle.Mantle;

import static slimeknights.mantle.registration.RegistrationHelper.injected;

/**
 * All recipe serializers registered under Mantles name
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MantleRecipeSerializers {
  public static final RecipeSerializer<?> CRAFTING_SHAPED_FALLBACK = injected();
  public static final RecipeSerializer<?> CRAFTING_SHAPED_RETEXTURED = injected();
}
