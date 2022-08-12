package slimeknights.mantle.mixin.common;

import io.github.fabricators_of_create.porting_lib.transfer.TransferUtil;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.impl.lookup.block.BlockApiLookupImpl;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import slimeknights.mantle.transfer.ItemStorageBlockDataHandler;
import slimeknights.mantle.transfer.item.ItemTransferable;

@Mixin(BlockApiLookupImpl.class)
public class BlockApiLookupImplMixin<A, C> {
  @Inject(method = "find", at = @At("RETURN"))
  public void mantle$syncItemStorage(Level world, BlockPos pos, @Nullable BlockState state, @Nullable BlockEntity blockEntity, C context, CallbackInfoReturnable<@Nullable A> cir) {
    A api = cir.getReturnValue();
    if (blockEntity != null && context instanceof Direction direction && api instanceof Storage<?> storage) {
      try (Transaction t = TransferUtil.getTransaction()) {
        for (StorageView<?> view : storage.iterable(t)) {
          if (view.getResource() instanceof ItemVariant) {
            if (blockEntity instanceof ItemTransferable transferable)
              ItemStorageBlockDataHandler.sendDataToClients(blockEntity, transferable.getItemHandler(direction).orElse(null));
            else
              ItemStorageBlockDataHandler.sendDataToClients(blockEntity, slimeknights.mantle.transfer.TransferUtil.simplifyItem((Storage<ItemVariant>) storage).orElse(null));
            break;
          }
        }
        t.abort();
      }
    }
  }
}
