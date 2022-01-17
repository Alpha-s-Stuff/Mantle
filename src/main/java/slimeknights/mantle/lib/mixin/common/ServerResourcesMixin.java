package slimeknights.mantle.lib.mixin.common;

import slimeknights.mantle.lib.event.DataPackReloadCallback;
import slimeknights.mantle.lib.util.MixinHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.commands.Commands;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.ServerResources;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ReloadableResourceManager;

@Mixin(ServerResources.class)
public abstract class ServerResourcesMixin {
	@Shadow
	@Final
	private ReloadableResourceManager resources;

	@Inject(method = "<init>",at = @At("TAIL"))
	public void create$DataPackRegistries(RegistryAccess registryAccess, Commands.CommandSelection commandSelection, int i, CallbackInfo ci) {
		for (PreparableReloadListener listener : DataPackReloadCallback.EVENT.invoker().onDataPackReload(MixinHelper.cast(this))) {
			resources.registerReloadListener(listener);
		}
	}
}
