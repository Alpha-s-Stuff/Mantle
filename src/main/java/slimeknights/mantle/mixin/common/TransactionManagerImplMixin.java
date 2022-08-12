package slimeknights.mantle.mixin.common;

import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import slimeknights.mantle.transfer.TransferUtil;

import java.util.List;

@Mixin(targets = {"net.fabricmc.fabric.impl.transfer.transaction.TransactionManagerImpl$TransactionImpl"}, remap = false)
public class TransactionManagerImplMixin {
  @Inject(method = "close(Lnet/fabricmc/fabric/api/transfer/v1/transaction/TransactionContext$Result;)V", at = @At("TAIL"))
  public void mantle$onActualClose(TransactionContext.Result result, CallbackInfo ci) {
    List<TransactionContext.OuterCloseCallback> outerCloseCallbacks = TransferUtil.getEndCallbacks((TransactionContext) this);
    outerCloseCallbacks.forEach(closeCallback -> closeCallback.afterOuterClose(result));
    outerCloseCallbacks.clear();
  }
}
