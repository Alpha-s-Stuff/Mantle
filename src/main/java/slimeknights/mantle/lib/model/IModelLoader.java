package slimeknights.mantle.lib.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.client.model.ModelProviderContext;
import net.fabricmc.fabric.api.client.model.ModelResourceProvider;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import slimeknights.mantle.Mantle;
import slimeknights.mantle.client.model.fluid.FluidTextureModel;
import slimeknights.mantle.lib.mixin.accessor.BlockModelAccessor;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;

public abstract class IModelLoader<T extends UnbakedModel> implements ModelResourceProvider, ResourceManagerReloadListener {

  public String type;

  public IModelLoader(String type) {
    this.type = type;
  }

  public IModelLoader(ResourceLocation type) {
    this.type = type.toString();
  }

  @Override
  public UnbakedModel loadModelResource(ResourceLocation resourceId, ModelProviderContext context) {
    try {
      if(resourceId.getNamespace().equals("minecraft") && resourceId.getPath().contains("builtin")) {
        return null;
      }

      JsonObject json = getModelJson(resourceId);

      return json.has("loader") && json.getAsJsonPrimitive("loader").getAsString().equals(type) ? loadJsonModelResource(resourceId, json, context) : null;

    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }

  public JsonDeserializationContext getContext() {
    return BlockModelAccessor.getGSON()::fromJson;
  }

  public abstract T read(JsonDeserializationContext deserializationContext, JsonObject modelContents, ModelProviderContext context);

  static JsonObject getModelJson(ResourceLocation location) throws IOException {
    return BlockModelAccessor.getGSON().fromJson(HBMABFIB.getModelJson(location), JsonObject.class);
  }
}
