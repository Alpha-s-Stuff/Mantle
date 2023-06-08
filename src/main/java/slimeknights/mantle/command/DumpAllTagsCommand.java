package slimeknights.mantle.command;

import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.ClickEvent.Action;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.TagEntry;
import net.minecraft.tags.TagManager;
import net.minecraft.util.GsonHelper;
import org.apache.commons.io.IOUtils;
import slimeknights.mantle.Mantle;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Map.Entry;

import static slimeknights.mantle.command.DumpTagCommand.GSON;

/**
 * Dumps all tags to a folder
 */
public class DumpAllTagsCommand {
  private static final String TAG_DUMP_PATH = "./mantle_data_dump";
  private static final int EXTENSION_LENGTH = ".json".length();

  /**
   * Registers this sub command with the root command
   * @param subCommand  Command builder
   */
  public static void register(LiteralArgumentBuilder<CommandSourceStack> subCommand) {
    subCommand.requires(sender -> sender.hasPermission(MantleCommand.PERMISSION_EDIT_SPAWN))
              .executes(DumpAllTagsCommand::runAll)
              .then(Commands.argument("type", RegistryArgument.registry()).suggests(MantleCommand.REGISTRY)
                            .executes(DumpAllTagsCommand::runType));
  }

  /** Gets the path for the output */
  protected static File getOutputFile(CommandContext<CommandSourceStack> context) {
    return context.getSource().getServer().getFile(TAG_DUMP_PATH);
  }

  /**
   * Makes a clickable text component for the output folder
   * @param file  File
   * @return  Clickable text component
   */
  protected static Component getOutputComponent(File file) {
    return Component.literal(file.getAbsolutePath()).withStyle(style -> style.withUnderlined(true).withClickEvent(new ClickEvent(Action.OPEN_FILE, file.getAbsolutePath())));
  }

  /** Dumps all tags to the game directory */
  private static int runAll(CommandContext<CommandSourceStack> context) {
    File output = getOutputFile(context);
    int tagsDumped = context.getSource().registryAccess().registries().mapToInt(r -> runForFolder(context, r.key(), output)).sum();
    // print the output path
    context.getSource().sendSuccess(() -> Component.translatable("command.mantle.dump_all_tags.success", getOutputComponent(output)), true);
    return tagsDumped;
  }

  /** Dumps a single type of tags to the game directory */
  private static int runType(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
    File output = getOutputFile(context);
    Registry<?> registry = RegistryArgument.getResult(context, "type");
    int result = runForFolder(context, registry.key(), output);
    // print result
    context.getSource().sendSuccess(() -> Component.translatable("command.mantle.dump_all_tags.type_success", registry.key().location(), getOutputComponent(output)), true);
    return result;
  }

  /**
   * Runs the view-tag command
   * @param context  Tag context
   * @return  Integer return
   */
  private static int runForFolder(CommandContext<CommandSourceStack> context, ResourceKey<? extends Registry<?>> key, File output) {
//    Map<ResourceLocation, Tag.Builder> foundTags = Maps.newHashMap();
//    MinecraftServer server = context.getSource().getServer();
//    ResourceManager manager = server.getResourceManager();
//    ResourceLocation tagType = key.location();
//
//    // iterate all tags from the datapack
//    String dataPackFolder = TagManager.getTagDir(key);
//    for (ResourceLocation resourcePath : manager.listResources(dataPackFolder, fileName -> fileName.getPath().endsWith(".json")).keySet()) {
//      String path = resourcePath.getPath();
//      ResourceLocation tagId = new ResourceLocation(resourcePath.getNamespace(), path.substring(dataPackFolder.length() + 1, path.length() - EXTENSION_LENGTH));
//      try {
//        for (Resource resource : manager.getResourceStack(resourcePath)) {
//          try (
//            Reader reader = resource.openAsReader()
//          ) {
//            JsonObject json = GsonHelper.fromJson(GSON, reader, JsonObject.class);
//            if (json == null) {
//              Mantle.logger.error("Couldn't load {} tag list {} from {} in data pack {} as it is empty or null", tagType, tagId, resourcePath, resource.sourcePackId());
//            } else {
//              // store by the resource path instead of the ID, thats the one we want at the end
//              foundTags.computeIfAbsent(resourcePath, id -> TagEntry.Builder.tag()).addFromJson(json, resource.sourcePackId());
//            }
//          } catch (RuntimeException | IOException ex) {
//            Mantle.logger.error("Couldn't read {} tag list {} from {} in data pack {}", tagType, tagId, resourcePath, resource.getSourceName(), ex);
//          } finally {
//            IOUtils.closeQuietly(resource);
//          }
//        }
//      } catch (IOException ex) {
//        Mantle.logger.error("Couldn't read {} tag list {} from {}", tagType, tagId, resourcePath, ex);
//      }
//    }
//
//    // save all tags
//    for (Entry<ResourceLocation, Tag.Builder> entry : foundTags.entrySet()) {
//      ResourceLocation location = entry.getKey();
//      Path path = output.toPath().resolve(location.getNamespace() + "/" + location.getPath());
//      try {
//        Files.createDirectories(path.getParent());
//        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
//          writer.write(GSON.toJson(entry.getValue().serializeToJson()));
//        }
//      } catch (IOException ex) {
//        Mantle.logger.error("Couldn't save tags to {}", path, ex);
//      }
//    }
//
//    return foundTags.size();
    return 0; // TODO: PORT
  }
}
