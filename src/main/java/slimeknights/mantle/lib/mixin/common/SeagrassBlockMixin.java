package slimeknights.mantle.lib.mixin.common;

import net.minecraft.world.level.block.SeagrassBlock;
import org.spongepowered.asm.mixin.Mixin;
import slimeknights.mantle.lib.extensions.IShearable;

@Mixin(SeagrassBlock.class)
public class SeagrassBlockMixin implements IShearable {
}
