package slimeknights.mantle.lib.mixin.client;

import slimeknights.mantle.lib.extensions.AbstractTextureExtension;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import net.minecraft.client.renderer.texture.AbstractTexture;

@Environment(EnvType.CLIENT)
@Mixin(AbstractTexture.class)
public abstract class AbstractTextureMixin implements AbstractTextureExtension {
	@Shadow
	protected boolean blur;
	@Shadow
	protected boolean mipmap;
	@Unique
	private boolean mantle$lastBlur;
	@Unique
	private boolean mantle$lastMipmap;

	@Shadow
	public abstract void setFilter(boolean blur, boolean mipmap);

	@Unique
	@Override
	public void mantle$setBlurMipmap(boolean blur, boolean mipmap) {
		this.mantle$lastBlur = this.blur;
		this.mantle$lastMipmap = this.mipmap;
		setFilter(blur, mipmap);
	}

	@Unique
	@Override
	public void mantle$restoreLastBlurMipmap() {
		setFilter(this.mantle$lastBlur, this.mantle$lastMipmap);
	}
}
