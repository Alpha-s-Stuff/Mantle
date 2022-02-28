package slimeknights.mantle.lib.extensions;

import net.minecraft.resources.ResourceLocation;

public interface RegistryNameProvider {
  default ResourceLocation getRegistryName() {
    return new ResourceLocation("empty");
  }
}
