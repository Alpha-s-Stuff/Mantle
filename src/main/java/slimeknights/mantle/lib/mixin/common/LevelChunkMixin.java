package slimeknights.mantle.lib.mixin.common;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.UpgradeData;
import net.minecraft.world.level.levelgen.blending.BlendingData;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import slimeknights.mantle.lib.block.CustomUpdateTagHandlingBlockEntity;
import slimeknights.mantle.lib.extensions.ChunkUnloadListeningBlockEntity;

@Mixin(LevelChunk.class)
public abstract class LevelChunkMixin extends ChunkAccess {
  public LevelChunkMixin(ChunkPos chunkPos, UpgradeData upgradeData, LevelHeightAccessor levelHeightAccessor, Registry<Biome> registry, long l, @Nullable LevelChunkSection[] levelChunkSections, @Nullable BlendingData blendingData) {
    super(chunkPos, upgradeData, levelHeightAccessor, registry, l, levelChunkSections, blendingData);
  }

  @Inject(method = "clearAllBlockEntities", at = @At("HEAD"))
  private void port_lib$blockEntityClear(CallbackInfo ci) {
    blockEntities.values().forEach(be -> {
      if (be instanceof ChunkUnloadListeningBlockEntity listener) {
        listener.onChunkUnloaded();
      }
    });
  }

  @Inject(method = "method_31716(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/BlockEntityType;Lnet/minecraft/nbt/CompoundTag;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/BlockEntity;load(Lnet/minecraft/nbt/CompoundTag;)V"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
  private void mantle$handleBlockEntityUpdateTag(BlockPos pos, BlockEntityType<?> type, CompoundTag tag, CallbackInfo ci, BlockEntity blockEntity) {
    if (blockEntity instanceof CustomUpdateTagHandlingBlockEntity handler) {
      handler.handleUpdateTag(tag);
      ci.cancel();
    }
  }
}
