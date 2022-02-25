package slimeknights.mantle.lib.extensions;

import net.minecraft.world.item.ItemStack;
import slimeknights.mantle.lib.util.ToolAction;

public interface ItemStackExtensions {
  default boolean canPerformAction(ToolAction toolAction)
  {
    return ((ItemExtensions)((ItemStack)(Object)this).getItem()).canPerformAction((ItemStack)(Object)this, toolAction);
  }

  static ItemStackExtensions cast(ItemStack stack) {
    return (ItemStackExtensions) (Object) stack;
  }
}
