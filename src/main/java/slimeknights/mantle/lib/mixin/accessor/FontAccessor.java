package slimeknights.mantle.lib.mixin.accessor;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.font.FontSet;
import net.minecraft.resources.ResourceLocation;

@Environment(EnvType.CLIENT)
@Mixin(Font.class)
public interface FontAccessor {
	@Invoker("getFontSet")
	FontSet mantle$getFontSet(ResourceLocation resourceLocation);
}
