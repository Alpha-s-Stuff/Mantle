package slimeknights.mantle.lib.mixin.common;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import slimeknights.mantle.lib.block.CustomUpdateTagHandlingBlockEntity;

@Mixin(LevelChunk.class)
public class LevelChunkMixin {
  @Inject(method = "method_31716(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/BlockEntityType;Lnet/minecraft/nbt/CompoundTag;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/BlockEntity;load(Lnet/minecraft/nbt/CompoundTag;)V"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
  private void mantle$handleBlockEntityUpdateTag(BlockPos pos, BlockEntityType<?> type, CompoundTag tag, CallbackInfo ci, BlockEntity blockEntity) {
    if (blockEntity instanceof CustomUpdateTagHandlingBlockEntity handler) {
      handler.handleUpdateTag(tag);
      ci.cancel();
    }
  }
}
