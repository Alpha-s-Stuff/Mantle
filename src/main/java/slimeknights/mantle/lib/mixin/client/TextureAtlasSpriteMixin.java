package slimeknights.mantle.lib.mixin.client;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import slimeknights.mantle.lib.extensions.TextureAtlasSpriteExtensions;

@Mixin(TextureAtlasSprite.class)
public class TextureAtlasSpriteMixin implements TextureAtlasSpriteExtensions {

  @Shadow
  @Final
  @Nullable
  private TextureAtlasSprite.AnimatedTexture animatedTexture;

  @Shadow
  @Final
  private int width;

  @Shadow
  @Final
  private int height;

  @Shadow
  @Final
  protected NativeImage[] mainImage;

  @Unique
  @Override
  public int getPixelRGBA(int frameIndex, int x, int y) {
    if (this.animatedTexture != null) {
      x += this.animatedTexture.getFrameX(frameIndex) * this.width;
      y += this.animatedTexture.getFrameY(frameIndex) * this.height;
    }

    return this.mainImage[0].getPixelRGBA(x, y);
  }
}
