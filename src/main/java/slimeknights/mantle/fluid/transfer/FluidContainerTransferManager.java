package slimeknights.mantle.fluid.transfer;

import com.google.common.collect.ImmutableSet;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import io.github.fabricators_of_create.porting_lib.crafting.CraftingHelper;
import io.github.fabricators_of_create.porting_lib.util.FluidStack;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import slimeknights.mantle.Mantle;
import slimeknights.mantle.data.GenericRegisteredSerializer;
import slimeknights.mantle.network.MantleNetwork;
import slimeknights.mantle.util.JsonHelper;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

/** Logic for filling and emptying fluid containers that are not fluid handlers */
@Log4j2
public class FluidContainerTransferManager extends SimpleJsonResourceReloadListener implements IdentifiableResourceReloadListener {
  /** Map of all modifier types that are expected to load in data packs */
  public static final GenericRegisteredSerializer<IFluidContainerTransfer> TRANSFER_LOADERS = new GenericRegisteredSerializer<>();
  /** Folder for saving the logic */
  public static final String FOLDER = "mantle/fluid_transfer";
  /** GSON instance */
  public static final Gson GSON = (new GsonBuilder())
    .registerTypeAdapter(ResourceLocation.class, new ResourceLocation.Serializer())
    .registerTypeHierarchyAdapter(IFluidContainerTransfer.class, TRANSFER_LOADERS)
    .setPrettyPrinting()
    .disableHtmlEscaping()
    .create();
  /** Singleton instance of the manager */
  public static final FluidContainerTransferManager INSTANCE = new FluidContainerTransferManager();

  /** List of loaded transfer logic, only exists serverside */
  private List<IFluidContainerTransfer> transfers = Collections.emptyList();

  /** Set of all items that match a recipe, exists on both sides */
  @Setter @Nullable
  private Set<Item> containerItems = Collections.emptySet();

  /** Condition context for tags */
//  private IContext context = IContext.EMPTY;

  private FluidContainerTransferManager() {
    super(GSON, FOLDER);
  }

  /** Lazily initializes the set of container items */
  protected Set<Item> getContainerItems() {
    if (this.containerItems == null) {
      ImmutableSet.Builder<Item> builder = ImmutableSet.builder();
      Consumer<Item> consumer = builder::add;
      for (IFluidContainerTransfer transfer : transfers) {
        transfer.addRepresentativeItems(consumer);
      }
      this.containerItems = builder.build();
    }
    return this.containerItems;
  }

  /** For internal use only */
  public void init() {
    ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(this);
//    MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, AddReloadListenerEvent.class, e -> {
//      e.addListener(this);
//      this.context = e.getConditionContext();
//    });
    ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register((player, joined) -> JsonHelper.syncPackets(player, joined, MantleNetwork.INSTANCE, new FluidContainerTransferPacket(this.getContainerItems())));
  }

  /** Loads transfer from JSON */
  @Nullable
  private IFluidContainerTransfer loadFluidTransfer(ResourceLocation key, JsonObject json) {
    try {
      if (!json.has(ResourceConditions.CONDITIONS_KEY) || CraftingHelper.processConditions(GsonHelper.getAsJsonArray(json, ResourceConditions.CONDITIONS_KEY))) {
        return GSON.fromJson(json, IFluidContainerTransfer.class);
      }
    } catch (JsonSyntaxException e) {
      log.error("Failed to load fluid container transfer info from {}", key, e);
    }
    return null;
  }

  @Override
  protected void apply(Map<ResourceLocation,JsonElement> splashList, ResourceManager manager, ProfilerFiller profiler) {
    long time = System.nanoTime();
    this.transfers = splashList.entrySet().stream()
                               .map(entry -> loadFluidTransfer(entry.getKey(), entry.getValue().getAsJsonObject()))
                               .filter(Objects::nonNull)
                               .toList();
    this.containerItems = null;
    log.info("Loaded {} dynamic modifiers in {} ms", transfers.size(), (System.nanoTime() - time) / 1000000f);
  }

  /**
   * Checks if the given stack could possibly match, used client side to determine if the fluid transfer falls back to opening the UI
   * @param stack  Stack to check
   * @return  True if a match is possible, basically just checks item ID
   */
  public boolean mayHaveTransfer(ItemStack stack) {
    return getContainerItems().contains(stack.getItem());
  }

  /** Gets the transfer for the given item and fluid, or null if its not a valid item and fluid */
  @Nullable
  public IFluidContainerTransfer getTransfer(ItemStack stack, FluidStack fluid) {
    for (IFluidContainerTransfer transfer : transfers) {
      if (transfer.matches(stack, fluid)) {
        return transfer;
      }
    }
    return null;
  }

  @Override
  public ResourceLocation getFabricId() {
    return Mantle.getResource("fluid_container_transfer_manager");
  }
}
