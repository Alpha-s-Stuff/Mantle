package slimeknights.mantle.lib.mixin.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagCollection;
import net.minecraft.tags.TagContainer;

@Mixin(TagContainer.class)
public interface TagContainerAccessor {

  @Accessor
  Map<ResourceKey<? extends Registry<?>>, TagCollection<?>> getCollections();
}
