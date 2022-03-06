package slimeknights.mantle.data.fabric;

import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resources.ResourceLocation;
import slimeknights.mantle.data.ISafeManagerReloadListener;

public abstract class IdentifiableISafeManagerReloadListener implements ISafeManagerReloadListener, IdentifiableResourceReloadListener {
  
  private final ResourceLocation id;
  
  protected IdentifiableISafeManagerReloadListener(ResourceLocation id) {
    this.id = id;
  }
  
  @Override
  public ResourceLocation getFabricId() {
    return id;
  }
}
