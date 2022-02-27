package slimeknights.mantle.lib.mixin.common;

import net.minecraft.world.level.block.VineBlock;
import org.spongepowered.asm.mixin.Mixin;
import slimeknights.mantle.lib.extensions.IShearable;

@Mixin(VineBlock.class)
public class VineBlockMixin implements IShearable {
}
