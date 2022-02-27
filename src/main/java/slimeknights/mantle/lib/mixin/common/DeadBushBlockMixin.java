package slimeknights.mantle.lib.mixin.common;

import net.minecraft.world.level.block.DeadBushBlock;
import org.spongepowered.asm.mixin.Mixin;
import slimeknights.mantle.lib.extensions.IShearable;

@Mixin(DeadBushBlock.class)
public class DeadBushBlockMixin implements IShearable {
}
