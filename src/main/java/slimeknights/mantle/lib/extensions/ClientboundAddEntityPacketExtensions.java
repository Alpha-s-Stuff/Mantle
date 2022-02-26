package slimeknights.mantle.lib.extensions;

import net.minecraft.network.FriendlyByteBuf;

public interface ClientboundAddEntityPacketExtensions {
	default FriendlyByteBuf mantle$getExtraDataBuf() {
    return null;
  }
}
