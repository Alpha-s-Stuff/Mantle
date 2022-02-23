package slimeknights.mantle.lib.extensions;

import net.minecraft.resources.ResourceLocation;

public interface ResourceLocationEx {
  default int compareNamespaced(ResourceLocation o) {
    return 0;
  }
}
