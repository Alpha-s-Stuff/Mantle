package slimeknights.mantle.block;

import net.fabricmc.fabric.api.registry.StrippableBlockRegistry;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;

import java.util.function.Supplier;

/** Log block that can be stripped */
public class StrippableLogBlock extends RotatedPillarBlock {
  private final Supplier<? extends Block> stripped;
  public StrippableLogBlock(Supplier<? extends Block> stripped, Properties properties) {
    super(properties);
    this.stripped = stripped;
    StrippableBlockRegistry.register(this, stripped.get());
  }
}
