package slimeknights.mantle.lib.mixin.common;

import net.minecraft.world.level.block.WebBlock;
import org.spongepowered.asm.mixin.Mixin;
import slimeknights.mantle.lib.extensions.IShearable;

@Mixin(WebBlock.class)
public class WebBlockMixin implements IShearable {
}
