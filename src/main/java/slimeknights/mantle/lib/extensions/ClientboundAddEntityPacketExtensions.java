package slimeknights.mantle.lib.extensions;

import net.minecraft.network.FriendlyByteBuf;

public interface ClientboundAddEntityPacketExtensions {
	FriendlyByteBuf create$getExtraDataBuf();
}
