package slimeknights.mantle.client.render;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import lombok.Getter;
import net.fabricmc.fabric.api.client.rendering.v1.CoreShaderRegistrationCallback;
import net.minecraft.client.renderer.ShaderInstance;
import slimeknights.mantle.Mantle;

import java.io.IOException;

public class MantleShaders {
  @Getter
  private static ShaderInstance blockFullBrightShader;
  @Getter
  private static ShaderInstance fluidShader;

  public static void registerShaders(CoreShaderRegistrationCallback.RegistrationContext registry) throws IOException {
    registry.register(
      Mantle.getResource("block_fullbright"), DefaultVertexFormat.BLOCK,
      shader -> blockFullBrightShader = shader
    );
    event.registerShader(
      new ShaderInstance(event.getResourceProvider(), Mantle.getResource("fluid"), DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP),
      shader -> fluidShader = shader
    );
  }
}
