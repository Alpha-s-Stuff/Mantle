package slimeknights.mantle.lib.mixin.common;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import slimeknights.mantle.lib.event.TagsUpdatedCallback;
import slimeknights.mantle.lib.util.MixinHelper;

import net.minecraft.tags.TagContainer;

@Mixin(TagContainer.class)
public class TagContainerMixin {
  @Inject(method = "bindToGlobal", at = @At("TAIL"))
  public void tagsEvent(CallbackInfo ci) {
    TagsUpdatedCallback.EVENT.invoker().onTagsUpdated(MixinHelper.cast(this));
  }
}
