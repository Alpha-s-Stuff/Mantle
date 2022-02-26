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
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;

public interface BlockStateExtensions {

	default boolean addRunningEffects(Level world, BlockPos pos, Entity entity) {
		return ((BlockState) this).getBlock().addRunningEffects((BlockState) this, world, pos, entity);
	}

	default boolean addLandingEffects(ServerLevel worldserver, BlockPos pos, BlockState state2, LivingEntity entity, int numberOfParticles) {
		return ((BlockState) this).getBlock().addLandingEffects((BlockState) this, worldserver, pos, state2, entity, numberOfParticles);
	}

	@Environment(EnvType.CLIENT)
	default boolean addDestroyEffects(Level world, BlockPos pos, ParticleEngine manager) {
		return ((BlockState) this).getBlock().addDestroyEffects((BlockState) this, world, pos, manager);
	}

	default boolean isFlammable(BlockGetter world, BlockPos pos, Direction face) {
		return ((BlockState) this).getBlock().isFlammable((BlockState) this, world, pos, face);
	}

	default int getFlammability(BlockGetter world, BlockPos pos, Direction face) {
		return ((BlockState) this).getBlock().getFlammability((BlockState) this, world, pos, face);
	}

	default void onNeighborChange(LevelReader world, BlockPos pos, BlockPos neighbor) {
    ((BlockState) this).getBlock().onNeighborChange((BlockState) this, world, pos, neighbor);
	}

	default float getSlipperiness(LevelReader world, BlockPos pos, @Nullable Entity entity)
	{
		return ((BlockState) this).getBlock().getSlipperiness((BlockState) this, world, pos, entity);
	}

  default boolean isBurning(BlockGetter world, BlockPos pos)
  {
    return ((BlockState) this).getBlock().isBurning((BlockState) this, world, pos);
  }
}
