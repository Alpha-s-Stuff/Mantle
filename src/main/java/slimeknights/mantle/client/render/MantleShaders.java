package slimeknights.mantle.client.render;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import io.github.fabricators_of_create.porting_lib.client_events.event.client.RegisterShadersCallback;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.server.packs.resources.ResourceProvider;
import slimeknights.mantle.Mantle;

import java.io.IOException;

public class MantleShaders {

  private static ShaderInstance blockFullBrightShader;

  public static void registerShaders(ResourceProvider resourceProvider, RegisterShadersCallback.ShaderRegistry registry) throws IOException {
    registry.registerShader(
      new ShaderInstance(resourceProvider, Mantle.getResource("block_fullbright").toString(), DefaultVertexFormat.BLOCK),
      shader -> blockFullBrightShader = shader
    );
  }

  public static ShaderInstance getBlockFullBrightShader() {
    return blockFullBrightShader;
  }
}
