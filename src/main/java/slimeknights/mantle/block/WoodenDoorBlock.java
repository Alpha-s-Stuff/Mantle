package slimeknights.mantle.block;

import io.github.fabricators_of_create.porting_lib.block.CustomPathNodeTypeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;

import javax.annotation.Nullable;

public class WoodenDoorBlock extends DoorBlock implements CustomPathNodeTypeBlock {
  public WoodenDoorBlock(Properties builder) {
    super(builder);
  }

  @Nullable
  @Override
  public BlockPathTypes getAiPathNodeType(BlockState state, BlockGetter world, BlockPos pos, @Nullable Mob entity) {
    return state.getValue(OPEN) ? BlockPathTypes.DOOR_OPEN : BlockPathTypes.DOOR_WOOD_CLOSED;
  }
}
