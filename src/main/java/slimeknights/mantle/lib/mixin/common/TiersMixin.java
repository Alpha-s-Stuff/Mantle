package slimeknights.mantle.lib.mixin.common;

import net.minecraft.world.item.Tiers;
import org.spongepowered.asm.mixin.Mixin;
import slimeknights.mantle.lib.extensions.TierExtension;
import slimeknights.mantle.lib.util.TagUtil;

@Mixin(Tiers.class)
public class TiersMixin implements TierExtension {
  @javax.annotation.Nullable public net.minecraft.tags.Tag<net.minecraft.world.level.block.Block> getTag() { return TagUtil.getTagFromVanillaTier((Tiers) (Object) this); }
}
