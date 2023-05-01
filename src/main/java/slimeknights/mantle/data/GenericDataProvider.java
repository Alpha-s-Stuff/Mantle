package slimeknights.mantle.data;

import com.google.common.hash.HashCode;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

/**
 * Generic logic to convert any serializable object into JSON
 */
@RequiredArgsConstructor
@Log4j2
public abstract class GenericDataProvider implements DataProvider {
  private static final Gson GSON = (new GsonBuilder())
    .registerTypeAdapter(ResourceLocation.class, new ResourceLocation.Serializer())
    .setPrettyPrinting()
    .disableHtmlEscaping()
    .create();

  protected final DataGenerator generator;
  private final PackType type;
  private final String folder;
  private final Gson gson;

  public GenericDataProvider(DataGenerator generator, PackType type, String folder) {
    this(generator, type, folder, GSON);
  }

  public GenericDataProvider(DataGenerator generator, String folder, Gson gson) {
    this(generator, PackType.SERVER_DATA, folder, gson);
  }

  public GenericDataProvider(DataGenerator generator, String folder) {
    this(generator, folder, GSON);
  }

  protected void saveThing(CachedOutput cache, ResourceLocation location, Object materialJson) {
    try {
      String json = gson.toJson(materialJson);
      Path path = this.generator.getOutputFolder().resolve(Paths.get(type.getDirectory(), location.getNamespace(), folder, location.getPath() + ".json"));
      cache.writeIfNeeded(path, json.getBytes(StandardCharsets.UTF_8), HashCode.fromInt(json.hashCode()));


    } catch (IOException e) {
      log.error("Couldn't create data for {}", location, e);
    }
  }
}
