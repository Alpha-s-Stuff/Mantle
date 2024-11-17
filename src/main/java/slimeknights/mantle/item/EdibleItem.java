package slimeknights.mantle.item;

import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import slimeknights.mantle.util.TranslationHelper;

import java.util.List;

public class EdibleItem extends Item {

  /** if false, does not display effects of food in tooltip */
  private final boolean displayEffectsTooltip;

  public EdibleItem(FoodProperties foodIn) {
    this(new Properties(), foodIn, true);
  }

  public EdibleItem(Properties properties, FoodProperties foodIn, boolean displayEffectsTooltip) {
    super(properties.food(foodIn));
    this.displayEffectsTooltip = displayEffectsTooltip;
  }

  @Override
  public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {
    TranslationHelper.addOptionalTooltip(stack, tooltip);

    if (this.displayEffectsTooltip) {
      FoodProperties food = stack.get(DataComponents.FOOD);
      if (food != null) {
        for (FoodProperties.PossibleEffect effect : food.effects()) {
          tooltip.add(Component.literal(I18n.get(effect.effect().getDescriptionId()).trim()).withStyle(ChatFormatting.GRAY));
        }
      }
    }

    super.appendHoverText(stack, context, tooltip, flagIn);
  }
}
