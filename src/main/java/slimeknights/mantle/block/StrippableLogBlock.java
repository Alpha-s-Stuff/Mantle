package slimeknights.mantle.block;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.phys.BlockHitResult;

/** Log block that can be stripped */
public class StrippableLogBlock extends RotatedPillarBlock {
  private final Supplier<? extends Block> stripped;
  public StrippableLogBlock(Supplier<? extends Block> stripped, Properties properties) {
    super(properties);
    this.stripped = stripped;
    UseBlockCallback.EVENT.register(this::getToolModifiedState);
  }


  // todo: port?
  @Nullable
  public InteractionResult getToolModifiedState(Player player, Level world, InteractionHand hand, BlockHitResult hitResult) {
    if (player.getItemInHand(hand).is(FabricToolTags.AXES)) {
      if(world.isClientSide)
        return InteractionResult.PASS;
      world.setBlock(hitResult.getBlockPos(), stripped.get().defaultBlockState().setValue(AXIS, world.getBlockState(hitResult.getBlockPos()).getValue(AXIS)) , Constants.BlockFlags.BLOCK_UPDATE);
    }
    return InteractionResult.PASS;
  }
}
