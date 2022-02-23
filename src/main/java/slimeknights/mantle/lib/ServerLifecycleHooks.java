package slimeknights.mantle.lib;

import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import slimeknights.mantle.lib.util.EnvExecutor;

public class ServerLifecycleHooks {
  private static MinecraftServer currentServer;

  public static void init() {
    EnvExecutor.runWhenOn(EnvType.SERVER, () -> () -> {
      ServerLifecycleEvents.SERVER_STARTING.register(server -> currentServer = server);
    });
  }

  public static MinecraftServer getCurrentServer() {
    return currentServer;
  }
}
