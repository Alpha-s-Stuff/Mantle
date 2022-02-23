package slimeknights.mantle.lib.util;

import com.mojang.blaze3d.platform.InputConstants;
import slimeknights.mantle.lib.mixin.accessor.KeyMappingAccessor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.client.KeyMapping;

@Environment(EnvType.CLIENT)
public final class KeyBindingHelper {
	public static InputConstants.Key getKeyCode(KeyMapping keyBinding) {
		return get(keyBinding).mantle$getKey();
	}

	private static KeyMappingAccessor get(KeyMapping keyBinding) {
		return MixinHelper.cast(keyBinding);
	}

	private KeyBindingHelper() { }
}
