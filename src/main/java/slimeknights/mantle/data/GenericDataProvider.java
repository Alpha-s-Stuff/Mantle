package slimeknights.mantle.data;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import slimeknights.mantle.util.JsonHelper;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/** Generic logic to convert any serializable object into JSON. */
@RequiredArgsConstructor
@Log4j2
public abstract class GenericDataProvider implements DataProvider {
  protected final FabricDataOutput output;
  private final PackType type;
  private final String folder;
  private final Gson gson;

  public GenericDataProvider(FabricDataOutput output, PackType type, String folder) {
    this(output, type, folder, JsonHelper.DEFAULT_GSON);
  }

  /**
   * Saves the given object to JSON
   * @param output     Output for writing
   * @param location   Location relative to this data provider's root
   * @param object     Object to save, will be converted using this provider's GSON instance
   */
  protected void saveJson(List<CompletableFuture<?>> futures, CachedOutput output, ResourceLocation location, Object object) {
    Path path = this.output.getOutputFolder().resolve(Paths.get(type.getDirectory(), location.getNamespace(), folder, location.getPath() + ".json"));
    futures.add(DataProvider.saveStable(output, gson.toJsonTree(object), path));
  }
}
