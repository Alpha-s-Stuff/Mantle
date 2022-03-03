package slimeknights.mantle.lib.mixin.client;

import net.minecraft.client.resources.model.ModelState;
import org.spongepowered.asm.mixin.Mixin;
import slimeknights.mantle.lib.extensions.ModelStateExtensions;

@Mixin(ModelState.class)
public interface ModelStateMixin extends ModelStateExtensions {
}
