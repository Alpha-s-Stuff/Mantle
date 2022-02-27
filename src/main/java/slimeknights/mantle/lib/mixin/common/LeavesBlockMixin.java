package slimeknights.mantle.lib.mixin.common;

import net.minecraft.world.level.block.LeavesBlock;
import org.spongepowered.asm.mixin.Mixin;
import slimeknights.mantle.lib.extensions.IShearable;

@Mixin(LeavesBlock.class)
public class LeavesBlockMixin implements IShearable {
}
