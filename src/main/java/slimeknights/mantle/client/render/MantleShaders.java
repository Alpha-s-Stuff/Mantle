package slimeknights.mantle.client.render;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.server.packs.resources.ResourceManager;
import slimeknights.mantle.Mantle;
import io.github.fabricators_of_create.porting_lib.event.client.RegisterShadersCallback;

import java.io.IOException;

public class MantleShaders {

  private static ShaderInstance blockFullBrightShader;

  public static void registerShaders(ResourceManager resourceManager, RegisterShadersCallback.ShaderRegistry registry) throws IOException {
    registry.registerShader(
      new ShaderInstance(resourceManager, Mantle.getResource("block_fullbright").toString(), DefaultVertexFormat.BLOCK),
      shader -> blockFullBrightShader = shader
    );
  }

  public static ShaderInstance getBlockFullBrightShader() {
    return blockFullBrightShader;
  }
}
