package slimeknights.mantle.lib.extensions;

import net.minecraft.world.item.ItemStack;
import slimeknights.mantle.lib.util.ToolAction;

public interface ItemStackExtensions {
  default boolean canPerformAction(ToolAction toolAction)
  {
    return ((ItemStack) this).getItem().canPerformAction((ItemStack) this, toolAction);
  }
}
