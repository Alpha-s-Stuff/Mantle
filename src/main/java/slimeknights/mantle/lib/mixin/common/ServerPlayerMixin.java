package slimeknights.mantle.lib.mixin.common;

import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import slimeknights.mantle.lib.event.ServerPlayerCreationCallback;
import slimeknights.mantle.lib.transfer.fluid.FluidStack;
import slimeknights.mantle.lib.transfer.fluid.IFluidHandler;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin extends Player {

	public ServerPlayerMixin(Level level, BlockPos blockPos, float f, GameProfile gameProfile) {
		super(level, blockPos, f, gameProfile);
	}

	@Unique
	private IFluidHandler mantle$lastViewedHandler = null;

	@Inject(method = "<init>", at = @At("RETURN"))
	private void mantle$init(MinecraftServer minecraftServer, ServerLevel serverLevel, GameProfile gameProfile, CallbackInfo ci) {
		ServerPlayerCreationCallback.EVENT.invoker().onCreate((ServerPlayer) (Object) this);
	}

	/**
	 * @return true if handlers are equivalent
	 */
	private boolean mantle$checkSameAndUpdate(IFluidHandler handler) {
		if (mantle$lastViewedHandler == null) return handler == null;

		if (mantle$lastViewedHandler.getTanks() != handler.getTanks()) {
			mantle$lastViewedHandler = handler;
			return false;
		}

		for (int i = 0; i < mantle$lastViewedHandler.getTanks(); i++) {
			FluidStack last = mantle$lastViewedHandler.getFluidInTank(i);
			FluidStack current = handler.getFluidInTank(i);
			if (!last.isFluidEqual(current)) {
				mantle$lastViewedHandler = handler;
				return false;
			}
		}

		return true;
	}
}
