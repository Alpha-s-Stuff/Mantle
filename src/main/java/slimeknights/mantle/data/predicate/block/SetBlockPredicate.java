package slimeknights.mantle.data.predicate.block;

import lombok.RequiredArgsConstructor;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import slimeknights.mantle.data.registry.GenericLoaderRegistry.IGenericLoader;
import slimeknights.mantle.data.loader.RegistrySetLoader;

import java.util.Set;

/**
 * Modifier matching a block
 */
@RequiredArgsConstructor
public class SetBlockPredicate implements BlockPredicate {
  public static final IGenericLoader<SetBlockPredicate> LOADER = new RegistrySetLoader<>("blocks", BuiltInRegistries.BLOCK, SetBlockPredicate::new, predicate -> predicate.blocks);

  private final Set<Block> blocks;

  @Override
  public boolean matches(BlockState state) {
    return blocks.contains(state.getBlock());
  }

  @Override
  public IGenericLoader<? extends BlockPredicate> getLoader() {
    return LOADER;
  }
}
