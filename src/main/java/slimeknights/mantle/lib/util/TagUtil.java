package slimeknights.mantle.lib.util;

import me.alphamode.forgetags.Tags;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.block.Block;
import slimeknights.mantle.lib.extensions.TierExtensions;

public class TagUtil {
  public static Tag<Block> getTagFromVanillaTier(Tiers tier)
  {
    return switch(tier)
      {
        case WOOD -> Tags.Blocks.NEEDS_WOOD_TOOL;
        case GOLD -> Tags.Blocks.NEEDS_GOLD_TOOL;
        case STONE -> BlockTags.NEEDS_STONE_TOOL;
        case IRON -> BlockTags.NEEDS_IRON_TOOL;
        case DIAMOND -> BlockTags.NEEDS_DIAMOND_TOOL;
        case NETHERITE -> Tags.Blocks.NEEDS_NETHERITE_TOOL;
      };
  }

  public static Tag<Block> getTagFromTier(Tier tier) {
    return ((TierExtensions)tier).getTag();
  }
}
