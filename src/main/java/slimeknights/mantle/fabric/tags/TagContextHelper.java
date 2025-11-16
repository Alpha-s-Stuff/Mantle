package slimeknights.mantle.fabric.tags;

import net.fabricmc.fabric.impl.resource.conditions.ResourceConditionsImpl;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class TagContextHelper {

  public static final Logger LOGGER = LoggerFactory.getLogger("Mantle Tag Context Helper");

  /**
   * Return the requested tag if available, or an empty tag otherwise.
   */
  public static <T> Collection<Holder<T>> getTag(TagKey<T> key) {
    return getAllTags(key.registry()).getOrDefault(key.location(), Set.of());
  }

  /**
   * Return all the loaded tags for the passed registry, or an empty map if none is available.
   * Note that the map and the tags are unmodifiable.
   */
  public static <T> Map<ResourceLocation, Collection<Holder<T>>> getAllTags(ResourceKey<? extends Registry<T>> registry) {
    @Nullable
    Map<ResourceKey<?>, Map<ResourceLocation, Collection<Holder<?>>>> allTags = ResourceConditionsImpl.LOADED_TAGS.get();

    if (allTags == null) {
      LOGGER.warn("Tags have not been loaded yet.");
      return Collections.emptyMap();
    }

    return (Map) allTags.getOrDefault(registry, Collections.emptyMap());
  }
}
