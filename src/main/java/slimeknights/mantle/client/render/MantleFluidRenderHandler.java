package slimeknights.mantle.client.render;

import io.github.fabricators_of_create.porting_lib.extensions.FluidExtensions;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.Nullable;

public class MantleFluidRenderHandler implements FluidRenderHandler {

  protected final ResourceLocation stillTexture;
  protected final ResourceLocation flowingTexture;
  protected final ResourceLocation overlayTexture;
  protected final TextureAtlasSprite[] icons;
  protected final Fluid fluid;

  public MantleFluidRenderHandler(ResourceLocation stillFluid, ResourceLocation flowingFluid, ResourceLocation overlayTexture, Fluid fluid) {
    this.stillTexture = stillFluid;
    this.flowingTexture = flowingFluid;
    this.overlayTexture = overlayTexture;
    this.icons = new TextureAtlasSprite[overlayTexture == null ? 2 : 3];
    this.fluid = fluid;
  }

  @Override
  public TextureAtlasSprite[] getFluidSprites(@Nullable BlockAndTintGetter view, @Nullable BlockPos pos, FluidState state) {
    return icons;
  }

  @Override
  public int getFluidColor(@Nullable BlockAndTintGetter view, @Nullable BlockPos pos, FluidState state) {
    return ((FluidExtensions) fluid).getAttributes().getColor();
  }

  @Override
  public void reloadTextures(TextureAtlas textureAtlas) {
    icons[0] = textureAtlas.getSprite(stillTexture);
    icons[1] = textureAtlas.getSprite(flowingTexture);
    if (overlayTexture != null) {
      icons[2] = textureAtlas.getSprite(overlayTexture);
    }
  }
}
