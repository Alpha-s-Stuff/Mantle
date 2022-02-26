package slimeknights.mantle.lib.mixin.client;

import net.minecraft.network.protocol.game.ClientboundUpdateRecipesPacket;
import net.minecraft.world.item.crafting.RecipeManager;
import slimeknights.mantle.lib.block.CustomDataPacketHandlingBlockEntity;
import slimeknights.mantle.lib.entity.ExtraSpawnDataEntity;
import slimeknights.mantle.lib.event.RecipesUpdatedCallback;
import slimeknights.mantle.lib.extensions.ClientboundAddEntityPacketExtensions;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.Connection;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.entity.BlockEntity;

@Environment(EnvType.CLIENT)
@Mixin(ClientPacketListener.class)
public abstract class ClientPacketListenerMixin {
	@Shadow
	@Final
	private Connection connection;

  @Shadow
  @Final
  private RecipeManager recipeManager;

  @Inject(
			method = "handleAddEntity",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/multiplayer/ClientLevel;putNonPlayerEntity(ILnet/minecraft/world/entity/Entity;)V",
					shift = Shift.AFTER
			),
			locals = LocalCapture.CAPTURE_FAILHARD
	)
	public void mantle$afterAddEntity(ClientboundAddEntityPacket packet, CallbackInfo ci, EntityType<?> entityType, Entity entity) {
		if (entity instanceof ExtraSpawnDataEntity extra) {
			FriendlyByteBuf extraData = packet.mantle$getExtraDataBuf();
			if (extraData != null) {
				extra.readSpawnData(extraData);
			}
		}
	}

	@Inject(method = "method_38542", at = @At("HEAD"), cancellable = true)
	public void mantle$handleCustomBlockEntity(ClientboundBlockEntityDataPacket packet, BlockEntity blockEntity, CallbackInfo ci) {
		if (blockEntity instanceof CustomDataPacketHandlingBlockEntity handler) {
			handler.onDataPacket(connection, packet);
			ci.cancel();
		}
	}

  @Inject(method = "handleUpdateRecipes", at = @At("TAIL"))
  public void mantle$updateRecipes(ClientboundUpdateRecipesPacket packet, CallbackInfo ci) {
    RecipesUpdatedCallback.EVENT.invoker().onRecipesUpdated(this.recipeManager);
  }
}
