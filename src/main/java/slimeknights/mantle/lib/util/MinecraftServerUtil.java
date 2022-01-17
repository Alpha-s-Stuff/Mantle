package slimeknights.mantle.lib.util;

import slimeknights.mantle.lib.mixin.accessor.MinecraftServerAccessor;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelStorageSource;

public final class MinecraftServerUtil {
	public static LevelStorageSource.LevelStorageAccess getAnvilConverterForAnvilFile(MinecraftServer minecraftServer) {
		return get(minecraftServer).create$getStorageSource();
	}

	private static MinecraftServerAccessor get(MinecraftServer minecraftServer) {
		return MixinHelper.cast(minecraftServer);
	}

	private MinecraftServerUtil() {}
}
