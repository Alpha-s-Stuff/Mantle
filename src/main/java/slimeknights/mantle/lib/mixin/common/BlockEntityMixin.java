package slimeknights.mantle.lib.mixin.common;

import slimeknights.mantle.lib.extensions.BlockEntityExtensions;
import slimeknights.mantle.lib.util.MixinHelper;
import slimeknights.mantle.lib.util.NBTSerializable;
import slimeknights.mantle.lib.util.TileEntityHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(BlockEntity.class)
public abstract class BlockEntityMixin implements BlockEntityExtensions, NBTSerializable {
	@Unique
	private CompoundTag mantle$extraCustomData;

	@Override
	public CompoundTag mantle$getExtraCustomData() {
		if (mantle$extraCustomData == null) {
			mantle$extraCustomData = new CompoundTag();
		}
		return mantle$extraCustomData;
	}

	@Inject(method = "load", at = @At("TAIL"))
	public void mantle$load(CompoundTag compoundNBT, CallbackInfo ci) {
		if (compoundNBT.contains(TileEntityHelper.EXTRA_DATA_KEY))
			this.mantle$extraCustomData = compoundNBT.getCompound(TileEntityHelper.EXTRA_DATA_KEY);
	}

	@Inject(method = "saveMetadata", at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/CompoundTag;putInt(Ljava/lang/String;I)V", ordinal = 2))
	private void mantle$saveMetadata(CompoundTag compoundNBT, CallbackInfo ci) {
		if (this.mantle$extraCustomData != null) {
			compoundNBT.put(TileEntityHelper.EXTRA_DATA_KEY, this.mantle$extraCustomData.copy());
		}
	}

	@Override
	public CompoundTag mantle$serializeNBT() {
		CompoundTag nbt = new CompoundTag();
		MixinHelper.<BlockEntity>cast(this).load(nbt);
		return nbt;
	}

	@Override
	public void mantle$deserializeNBT(CompoundTag nbt) {
		mantle$deserializeNBT(null, nbt);
	}

	public void mantle$deserializeNBT(BlockState state, CompoundTag nbt) {
		MixinHelper.<BlockEntity>cast(this).load(nbt);
	}
}
