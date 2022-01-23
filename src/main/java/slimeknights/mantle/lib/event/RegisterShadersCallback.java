package slimeknights.mantle.lib.event;

import com.mojang.datafixers.util.Pair;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

public interface RegisterShadersCallback {
  Event<RegisterShadersCallback> EVENT = EventFactory.createArrayBacked(RegisterShadersCallback.class, callbacks -> (resourceManager, registry) -> {
    for(RegisterShadersCallback event : callbacks) {
      event.onShaderReload(resourceManager, registry);
    }
  });

    static class ShaderRegistry {
      private final List<Pair<ShaderInstance, Consumer<ShaderInstance>>> shaderList;

      public ShaderRegistry(final List<Pair<ShaderInstance, Consumer<ShaderInstance>>> shaderList) {
        this.shaderList = shaderList;
      }

      public void registerShader(ShaderInstance shaderInstance, Consumer<ShaderInstance> onLoaded) {
        shaderList.add(Pair.of(shaderInstance, onLoaded));
      }
    }

    void onShaderReload(ResourceManager resourceManager, ShaderRegistry registry) throws IOException;
}
