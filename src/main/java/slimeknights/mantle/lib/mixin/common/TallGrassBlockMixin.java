package slimeknights.mantle.lib.mixin.common;

import net.minecraft.world.level.block.TallGrassBlock;
import org.spongepowered.asm.mixin.Mixin;
import slimeknights.mantle.lib.extensions.IShearable;

@Mixin(TallGrassBlock.class)
public class TallGrassBlockMixin implements IShearable {
}
