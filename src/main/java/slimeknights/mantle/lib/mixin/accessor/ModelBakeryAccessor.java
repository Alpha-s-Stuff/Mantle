package slimeknights.mantle.lib.mixin.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.function.Predicate;

import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

@Mixin(ModelBakery.class)
public interface ModelBakeryAccessor {

  @Invoker
  static Predicate<BlockState> callPredicate(StateDefinition<Block, BlockState> container, String variant) {
    throw new UnsupportedOperationException();
  }
}
