package slimeknights.mantle.block;

import io.github.fabricators_of_create.porting_lib.tags.ToolTags;
import io.github.fabricators_of_create.porting_lib.util.Constants;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

/** Log block that can be stripped */
public class StrippableLogBlock extends RotatedPillarBlock {
  private final Supplier<? extends Block> stripped;
  public StrippableLogBlock(Supplier<? extends Block> stripped, Properties properties) {
    super(properties);
    this.stripped = stripped;
    UseBlockCallback.EVENT.register(this::getToolModifiedState);
  }

  @Nullable
  public InteractionResult getToolModifiedState(Player player, Level world, InteractionHand hand, BlockHitResult hitResult) {
    BlockState state = world.getBlockState(hitResult.getBlockPos());
    if (player.getItemInHand(hand).is(ToolTags.AXES) && state.is(this)) {
      world.setBlockAndUpdate(hitResult.getBlockPos(), stripped.get().defaultBlockState().setValue(AXIS, state.getValue(AXIS)));
      return InteractionResult.SUCCESS;
    }
    return InteractionResult.PASS;
  }
}
