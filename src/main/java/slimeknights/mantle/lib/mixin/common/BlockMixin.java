package slimeknights.mantle.lib.mixin.common;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Unique;
import slimeknights.mantle.lib.extensions.BlockExtensions;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import slimeknights.mantle.lib.extensions.RegistryNameProvider;
import slimeknights.mantle.lib.util.MixinHelper;

@Mixin(Block.class)
public abstract class BlockMixin extends BlockBehaviour implements BlockExtensions, RegistryNameProvider {

	private BlockMixin(Properties properties) {
		super(properties);
	}

	@Shadow
	public abstract SoundType getSoundType(BlockState blockState);

  @Unique
	@Override
	public SoundType getSoundType(BlockState state, LevelReader world, BlockPos pos, @Nullable Entity entity) {
		return getSoundType(state);
	}

  @Unique
	@Override
	public int getLightEmission(BlockState state, BlockGetter world, BlockPos pos) {
		return state.getLightEmission();
	}

  @Override
  public ResourceLocation getRegistryName() {
    return Registry.BLOCK.getKey(MixinHelper.cast(this));
  }
}
