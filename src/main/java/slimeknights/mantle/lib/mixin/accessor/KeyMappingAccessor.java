package slimeknights.mantle.lib.mixin.accessor;

import com.mojang.blaze3d.platform.InputConstants.Key;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.KeyMapping;

@Environment(EnvType.CLIENT)
@Mixin(KeyMapping.class)
public interface KeyMappingAccessor {
	@Accessor("key")
	Key create$getKey();
}
