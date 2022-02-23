package slimeknights.mantle.lib.mixin.common;

import net.minecraft.world.item.Tier;
import org.spongepowered.asm.mixin.Mixin;
import slimeknights.mantle.lib.extensions.TierExtension;

@Mixin(Tier.class)
public interface TierMixin extends TierExtension {
}
