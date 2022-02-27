package slimeknights.mantle.lib.extensions;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import javax.annotation.Nullable;

import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HalfTransparentBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;

public interface BlockExtensions {
	default boolean addRunningEffects(BlockState state, Level world, BlockPos pos, Entity entity) {
		return false;
	}

	default boolean addLandingEffects(BlockState state1, ServerLevel worldserver, BlockPos pos, BlockState state2, LivingEntity entity, int numberOfParticles) {
		return false;
	}

	@Environment(EnvType.CLIENT)
	default boolean addDestroyEffects(BlockState state, Level world, BlockPos pos, ParticleEngine manager) {
		return false;
	}

	default boolean isFlammable(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
		return state.getFlammability(world, pos, face) > 0;
	}

	default int getFlammability(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
		return ((FireBlockExtensions) Blocks.FIRE).invokeGetBurnOdd(state);
	}

  default boolean isBurning(BlockState state, BlockGetter world, BlockPos pos) {
    return this == Blocks.FIRE || this == Blocks.LAVA;
  }

	default SoundType getSoundType(BlockState state, LevelReader world, BlockPos pos, @Nullable Entity entity) {
		return ((Block) this).getSoundType(state);
	}

	default int getLightEmission(BlockState state, BlockGetter world, BlockPos pos) {
		return state.getLightEmission();
	}

	default boolean shouldDisplayFluidOverlay(BlockState state, BlockAndTintGetter world, BlockPos pos, FluidState fluidState) {
		return state.getBlock() instanceof HalfTransparentBlock || state.getBlock() instanceof LeavesBlock;
	}

	default void onNeighborChange(BlockState state, LevelReader world, BlockPos pos, BlockPos neighbor) {}

	default float getSlipperiness(BlockState state, LevelReader world, BlockPos pos, Entity entity) {
		return ((Block) this).getFriction();
	}

  default boolean onDestroyedByPlayer(BlockState state, Level world, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
    ((Block) this).playerWillDestroy(world, pos, state, player);
    return world.setBlock(pos, fluid.createLegacyBlock(), world.isClientSide ? 11 : 3);
  }

  default public boolean canHarvestBlock(BlockState state, BlockGetter world, BlockPos pos, Player player) {
    return player.hasCorrectToolForDrops(state);
  }

  @Nullable
  default BlockPathTypes getAiPathNodeType(BlockState state, BlockGetter world, BlockPos pos, @Nullable Mob entity) {
    return state.getBlock() == Blocks.LAVA ? BlockPathTypes.LAVA : state.isBurning(world, pos) ? BlockPathTypes.DAMAGE_FIRE : null;
  }
}
