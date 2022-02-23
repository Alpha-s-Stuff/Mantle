package slimeknights.mantle.lib.mixin.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(StairBlock.class)
public interface StairBlockAccessor {
	@Invoker("<init>")
	static StairBlock mantle$init(BlockState baseBlockState, BlockBehaviour.Properties properties) {
		throw new AssertionError("Mixin application failed!");
	}
}
