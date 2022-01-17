package slimeknights.mantle.lib.mixin.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;

@Mixin(Item.class)
public interface ItemAccessor {

  @Invoker
  static BlockHitResult callGetPlayerPOVHitResult(Level level, Player player, ClipContext.Fluid fluidMode) {
    throw new UnsupportedOperationException();
  }
}
