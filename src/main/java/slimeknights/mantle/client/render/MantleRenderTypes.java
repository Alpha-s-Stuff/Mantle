package slimeknights.mantle.client.render;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import slimeknights.mantle.Mantle;

import static com.mojang.blaze3d.vertex.VertexFormatElement.COLOR;
import static com.mojang.blaze3d.vertex.VertexFormatElement.NORMAL;
import static com.mojang.blaze3d.vertex.VertexFormatElement.POSITION;
import static com.mojang.blaze3d.vertex.VertexFormatElement.UV0;
import static com.mojang.blaze3d.vertex.VertexFormatElement.UV1;
import static com.mojang.blaze3d.vertex.VertexFormatElement.UV2;

/**
 * Class for render types defined by Mantle
 */
public class MantleRenderTypes extends RenderType {

  private MantleRenderTypes(String name, VertexFormat format, Mode mode, int bufferSize, boolean useDelegate, boolean needsSorting, Runnable setupTaskIn, Runnable clearTaskIn) {
    super(name, format, mode, bufferSize, useDelegate, needsSorting, setupTaskIn, clearTaskIn);
  }

  /**
   * Render type used for the fluid renderer
   */
  public static final RenderType FLUID = create(
    Mantle.modId + ":block_render_type",
    DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP, VertexFormat.Mode.QUADS, 256, false, true,
    RenderType.CompositeState.builder()
      .setLightmapState(LIGHTMAP)
      .setShaderState(POSITION_COLOR_TEX_LIGHTMAP_SHADER)
      .setTextureState(BLOCK_SHEET_MIPPED)
      .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
      .createCompositeState(false));

  /**
   * Render type used for the structure renderer
   */
  public static final VertexFormat BLOCK_WITH_OVERLAY = VertexFormat.builder().add("Position", POSITION).add("Color", COLOR).add("UV0", UV0).add("UV1", UV1).add("UV2", UV2).add("Normal", NORMAL).padding(1).build();

  public static final RenderType TRANSLUCENT_FULLBRIGHT = create(
    Mantle.modId + ":translucent_fullbright",
    BLOCK_WITH_OVERLAY, Mode.QUADS, 256, false, false,
    RenderType.CompositeState.builder()
      .setShaderState(new RenderStateShard.ShaderStateShard(MantleShaders::getBlockFullBrightShader))
      .setLightmapState(new RenderStateShard.LightmapStateShard(false))
      .setOverlayState(OVERLAY)
      .setTextureState(BLOCK_SHEET_MIPPED)
      .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
      .createCompositeState(false));
}
