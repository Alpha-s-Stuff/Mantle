package slimeknights.mantle.lib.mixin.common;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.animal.MushroomCow;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import slimeknights.mantle.lib.extensions.IShearable;

@Mixin(MushroomCow.class)
public abstract class MushroomCowMixin implements IShearable {

  @Shadow
  public abstract boolean readyForShearing();

  @Shadow
  public abstract void shear(SoundSource category);

  @Shadow
  public abstract MushroomCow.MushroomType getMushroomType();

  @Unique
  @Override
  public boolean isShearable(@javax.annotation.Nonnull ItemStack item, Level world, BlockPos pos) {
    return readyForShearing();
  }

  @Override
  public java.util.List<ItemStack> onSheared(@javax.annotation.Nullable Player player, @javax.annotation.Nonnull ItemStack item, Level world, BlockPos pos, int fortune) {
    shear(player == null ? SoundSource.BLOCKS : SoundSource.PLAYERS);
    java.util.List<ItemStack> items = new java.util.ArrayList<>();
    for(int i = 0; i < 5; ++i) {
      items.add(new ItemStack(this.getMushroomType().blockState.getBlock()));
    }
    return items;
  }
}
