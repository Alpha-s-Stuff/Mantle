package slimeknights.mantle.lib.extensions;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

public interface FireBlockExtensions {
	default boolean canCatchFire(BlockGetter world, BlockPos pos, Direction face) {
    return false;
  }

	default int invokeGetBurnOdd(BlockState state) {
    return 0;
  }
}
