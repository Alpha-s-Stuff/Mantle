package slimeknights.mantle.client.render;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.fabricmc.fabric.api.client.rendering.v1.CoreShaderRegistrationCallback;
import net.minecraft.client.renderer.ShaderInstance;
import slimeknights.mantle.Mantle;

import java.io.IOException;

public class MantleShaders {

  private static ShaderInstance blockFullBrightShader;

  public static void registerShaders(CoreShaderRegistrationCallback.RegistrationContext registry) throws IOException {
    registry.register(
      Mantle.getResource("block_fullbright"), DefaultVertexFormat.BLOCK,
      shader -> blockFullBrightShader = shader
    );
  }

  public static ShaderInstance getBlockFullBrightShader() {
    return blockFullBrightShader;
  }
}
