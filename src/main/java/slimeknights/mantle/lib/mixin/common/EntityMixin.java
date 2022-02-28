package slimeknights.mantle.lib.mixin.common;

import slimeknights.mantle.lib.event.EntityEvents;
import slimeknights.mantle.lib.event.StartRidingCallback;
import slimeknights.mantle.lib.extensions.EntityExtensions;
import slimeknights.mantle.lib.util.EntityHelper;
import slimeknights.mantle.lib.util.MixinHelper;
import slimeknights.mantle.lib.util.NBTSerializable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import java.util.Collection;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

@Mixin(Entity.class)
public abstract class EntityMixin implements EntityExtensions, NBTSerializable {
	@Shadow
	public Level level;
	@Shadow
	private float eyeHeight;
	@Unique
	private CompoundTag mantle$extraCustomData;
	@Unique
	private Collection<ItemEntity> mantle$captureDrops = null;

	@Shadow
	protected abstract void readAdditionalSaveData(CompoundTag compoundTag);

	@Inject(at = @At("TAIL"), method = "<init>")
	public void mantle$entityInit(EntityType<?> entityType, Level world, CallbackInfo ci) {
		int newEyeHeight = EntityEvents.EYE_HEIGHT.invoker().onEntitySize((Entity) (Object) this);
		if (newEyeHeight != -1)
			eyeHeight = newEyeHeight;
	}

	// CAPTURE DROPS

	@Inject(
			method = "spawnAtLocation(Lnet/minecraft/world/item/ItemStack;F)Lnet/minecraft/world/entity/item/ItemEntity;",
			locals = LocalCapture.CAPTURE_FAILHARD,
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/entity/item/ItemEntity;setDefaultPickUpDelay()V",
					shift = At.Shift.AFTER
			),
			cancellable = true
	)
	public void mantle$spawnAtLocation(ItemStack stack, float f, CallbackInfoReturnable<ItemEntity> cir, ItemEntity itemEntity) {
		if (mantle$captureDrops != null) mantle$captureDrops.add(itemEntity);
		else cir.cancel();
	}

	@Unique
	@Override
	public Collection<ItemEntity> captureDrops() {
		return mantle$captureDrops;
	}

	@Unique
	@Override
	public Collection<ItemEntity> captureDrops(Collection<ItemEntity> value) {
		Collection<ItemEntity> ret = mantle$captureDrops;
		mantle$captureDrops = value;
		return ret;
	}

	// EXTRA CUSTOM DATA

	@Inject(method = "saveWithoutId", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;addAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V"))
	public void mantle$beforeWriteCustomData(CompoundTag tag, CallbackInfoReturnable<CompoundTag> cir) {
		if (mantle$extraCustomData != null && !mantle$extraCustomData.isEmpty()) {
			tag.put(EntityHelper.EXTRA_DATA_KEY, mantle$extraCustomData);
		}
	}

	@Inject(method = "load", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;readAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V"))
	public void mantle$beforeReadCustomData(CompoundTag tag, CallbackInfo ci) {
		if (tag.contains(EntityHelper.EXTRA_DATA_KEY)) {
			mantle$extraCustomData = tag.getCompound(EntityHelper.EXTRA_DATA_KEY);
		}
	}

	// RUNNING EFFECTS

	@Inject(
			method = "spawnSprintParticle",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/level/Level;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;",
					shift = At.Shift.AFTER
			),
			locals = LocalCapture.CAPTURE_FAILHARD,
			cancellable = true
	)
	public void mantle$spawnSprintParticle(CallbackInfo ci, int i, int j, int k, BlockPos blockPos) {
		if (level.getBlockState(blockPos).addRunningEffects(level, blockPos, MixinHelper.cast(this))) {
			ci.cancel();
		}
	}

	// (did someone intend to write something here?)


	@Inject(
			method = "startRiding(Lnet/minecraft/world/entity/Entity;Z)Z",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/entity/Entity;isPassenger()Z",
					shift = At.Shift.BEFORE
			),
			cancellable = true
	)
	public void mantle$startRiding(Entity entity, boolean bl, CallbackInfoReturnable<Boolean> cir) {
		if (StartRidingCallback.EVENT.invoker().onStartRiding(MixinHelper.cast(this), entity) == InteractionResult.FAIL) {
			cir.setReturnValue(false);
		}
	}

  @Inject(method = "remove", at = @At("TAIL"))
  public void mantle$onEntityRemove(Entity.RemovalReason reason, CallbackInfo ci) {
    EntityEvents.ON_REMOVE.invoker().onRemove((Entity) (Object) this, reason);
  }

	@Unique
	@Override
	public CompoundTag getExtraCustomData() {
		if (mantle$extraCustomData == null) {
			mantle$extraCustomData = new CompoundTag();
		}
		return mantle$extraCustomData;
	}

	@Unique
	@Override
	public CompoundTag mantle$serializeNBT() {
		CompoundTag nbt = new CompoundTag();
		String id = EntityHelper.getEntityString(MixinHelper.cast(this));

		if (id != null) {
			nbt.putString("id", id);
		}

		return nbt;
	}

	@Unique
	@Override
	public void mantle$deserializeNBT(CompoundTag nbt) {
		readAdditionalSaveData(nbt);
	}
}
