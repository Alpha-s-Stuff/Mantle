package slimeknights.mantle.lib.model;

import com.google.common.base.Charsets;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;

/**
 * Hydos' Based Model Api Because Fabric Is Bad (why tf am I including this)
 */
public class HBMABFIB {

  public static BlockModel getModelSafe(ResourceLocation resourceId) {
    try {
      return BlockModel.fromStream(getModelJson(resourceId));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  static BufferedReader getModelJson(ResourceLocation location) throws IOException {
    ResourceLocation file = new ResourceLocation(location.getNamespace(), "models/" + location.getPath() + ".json");
    Resource resource = Minecraft.getInstance().getResourceManager().getResource(file);
    return new BufferedReader(new InputStreamReader(resource.getInputStream(), Charsets.UTF_8));
  }
}
