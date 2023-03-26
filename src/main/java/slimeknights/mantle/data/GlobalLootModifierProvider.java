package slimeknights.mantle.data;

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import io.github.fabricators_of_create.porting_lib.PortingLibRegistries;
import io.github.fabricators_of_create.porting_lib.loot.IGlobalLootModifier;
import io.github.fabricators_of_create.porting_lib.loot.LootModifier;
import io.github.fabricators_of_create.porting_lib.util.LamdbaExceptionUtils;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Provider for forge's GlobalLootModifier system. See {@link LootModifier} and {@link GlobalLootModifierSerializer}.
 *
 * This provider only requires implementing {@link #start()} and calling {@link #add} from it.
 */
public abstract class GlobalLootModifierProvider implements DataProvider
{
  private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
  private final FabricDataOutput gen;
  private final String modid;
  private final Map<String, Tuple<Codec<? extends IGlobalLootModifier>, JsonObject>> toSerialize = new HashMap<>();
  private boolean replace = false;

  public GlobalLootModifierProvider(FabricDataOutput gen, String modid)
  {
    this.gen = gen;
    this.modid = modid;
  }

  /**
   * Sets the "replace" key in global_loot_modifiers to true.
   */
  protected void replacing()
  {
    this.replace = true;
  }

  /**
   * Call {@link #add} here, which will pass in the necessary information to write the jsons.
   */
  protected abstract void start();

  @Override
  public CompletableFuture<?> run(CachedOutput cache) {
    start();

    Path forgePath = gen.getOutputFolder().resolve("data/forge/loot_modifiers/global_loot_modifiers.json");
    String modPath = "data/" + modid + "/loot_modifiers/";
    List<ResourceLocation> entries = new ArrayList<>();
    ImmutableList.Builder<CompletableFuture<?>> futuresBuilder = new ImmutableList.Builder<>();

    toSerialize.forEach(LamdbaExceptionUtils.rethrowBiConsumer((name, pair) ->
    {
      entries.add(new ResourceLocation(modid, name));
      Path modifierPath = gen.getOutputFolder().resolve(modPath + name + ".json");

      JsonObject json = pair.getB();
      json.addProperty("type", PortingLibRegistries.GLOBAL_LOOT_MODIFIER_SERIALIZERS.get().getKey(pair.getA()).toString());

      futuresBuilder.add(DataProvider.saveStable(cache, json, modifierPath));
    }));

    JsonObject forgeJson = new JsonObject();
    forgeJson.addProperty("replace", this.replace);
    forgeJson.add("entries", GSON.toJsonTree(entries.stream().map(ResourceLocation::toString).collect(Collectors.toList())));

    futuresBuilder.add(DataProvider.saveStable(cache, forgeJson, forgePath));
    return CompletableFuture.allOf(futuresBuilder.build().toArray(CompletableFuture[]::new));
  }

  /**
   * Passes in the data needed to create the file without any extra objects.
   *
   * @param modifier      The name of the modifier, which will be the file name.
   * @param serializer    The serializer of this modifier.
   */
  public <T extends IGlobalLootModifier> void add(String modifier, Codec<T> serializer, T instance)
  {
    this.toSerialize.put(modifier, new Tuple<>(serializer, serializer.encodeStart(JsonOps.INSTANCE, instance).getOrThrow(false, System.out::println).getAsJsonObject()));
  }

  @Override
  public String getName()
  {
    return "Global Loot Modifiers : " + modid;
  }
}
