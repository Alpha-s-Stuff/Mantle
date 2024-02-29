package slimeknights.mantle.data;

import com.google.common.hash.Hashing;
import com.google.common.hash.HashingOutputStream;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.Util;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;

/**
 * Generic logic to convert any serializable object into JSON.
 * TODO 1.19: move to {@link slimeknights.mantle.datagen}
 */
@RequiredArgsConstructor
@Log4j2
public abstract class GenericDataProvider implements DataProvider {
  private static final Gson GSON = (new GsonBuilder())
    .registerTypeAdapter(ResourceLocation.class, new ResourceLocation.Serializer())
    .setPrettyPrinting()
    .disableHtmlEscaping()
    .create();

  protected final FabricDataOutput generator;
  private final PackType type;
  private final String folder;
  private final Gson gson;

  public GenericDataProvider(FabricDataOutput generator, PackType type, String folder) {
    this(generator, type, folder, GSON);
  }

  public GenericDataProvider(FabricDataOutput generator, String folder, Gson gson) {
    this(generator, PackType.SERVER_DATA, folder, gson);
  }

  public GenericDataProvider(FabricDataOutput generator, String folder) {
    this(generator, folder, GSON);
  }

  protected CompletableFuture<?> saveThing(CachedOutput cache, ResourceLocation location, Object materialJson) {
    return CompletableFuture.runAsync(() -> {
      try {
        String json = gson.toJson(materialJson);
        Path path = this.generator.getOutputFolder().resolve(Paths.get(type.getDirectory(), location.getNamespace(), folder, location.getPath() + ".json"));
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        HashingOutputStream hashingOutputStream = new HashingOutputStream(Hashing.sha1(), byteArrayOutputStream);
        Writer writer = new OutputStreamWriter(hashingOutputStream, StandardCharsets.UTF_8);
        writer.write(json);
        writer.close();
        cache.writeIfNeeded(path, byteArrayOutputStream.toByteArray(), hashingOutputStream.hash());
      } catch (IOException e) {
        log.error("Couldn't create data for {}", location, e);
      }
    }, Util.backgroundExecutor());
  }
}
