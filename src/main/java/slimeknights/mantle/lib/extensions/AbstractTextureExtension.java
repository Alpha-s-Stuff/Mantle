package slimeknights.mantle.lib.extensions;

public interface AbstractTextureExtension {
	void create$setBlurMipmap(boolean blur, boolean mipmap);
	void create$restoreLastBlurMipmap();
}
