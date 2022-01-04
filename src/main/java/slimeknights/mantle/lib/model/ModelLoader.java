package slimeknights.mantle.lib.model;

import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import slimeknights.mantle.Mantle;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;

public class ModelLoader implements IdentifiableResourceReloadListener {

  @Override
  public ResourceLocation getFabricId() {
    return Mantle.getResource("model_loader");
  }

  @Override
  public CompletableFuture<Void> reload(PreparationBarrier preparationBarrier, ResourceManager resourceManager, ProfilerFiller preparationsProfiler, ProfilerFiller reloadProfiler, Executor backgroundExecutor, Executor gameExecutor) {
    return null;
  }
}
