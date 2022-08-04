package slimeknights.mantle.mixin.client;

import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ItemStorage.class, remap = false)
public class ItemStorageMixin {
  @Inject(method = "lambda$static$0", at = @At("HEAD"), cancellable = true)
  private static void mantle$allowClientTransfer(Level world, BlockPos pos, BlockState state, BlockEntity blockEntity, Direction context, CallbackInfoReturnable<Storage> cir) {
    cir.setReturnValue(null);
  }
}
