package slimeknights.mantle.fluid.transfer;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import io.github.fabricators_of_create.porting_lib.fluids.FluidStack;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.resource.conditions.v1.ConditionJsonProvider;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceCondition;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditions;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluid;
import slimeknights.mantle.data.GenericDataProvider;
import slimeknights.mantle.recipe.helper.ItemOutput;
import slimeknights.mantle.recipe.ingredient.FluidIngredient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/** Data gen for fluid transfer logic */
@SuppressWarnings("unused")
public abstract class AbstractFluidContainerTransferProvider extends GenericDataProvider {
  private final Map<ResourceLocation,TransferJson> allTransfers = new HashMap<>();
  private final String modId;

  public AbstractFluidContainerTransferProvider(FabricDataOutput generator, String modId) {
    super(generator, PackType.SERVER_DATA, FluidContainerTransferManager.FOLDER, FluidContainerTransferManager.GSON);
    this.modId = modId;
  }

  /** Function to add all relevant transfers */
  protected abstract void addTransfers();

  /** Adds a transfer to be saved */
  protected void addTransfer(ResourceLocation id, IFluidContainerTransfer transfer, ResourceCondition... conditions) {
    TransferJson previous = allTransfers.putIfAbsent(id, new TransferJson(transfer, conditions));
    if (previous != null) {
      throw new IllegalArgumentException("Duplicate fluid container transfer " + id);
    }
  }

  /** Adds a transfer to be saved */
  protected void addTransfer(String name, IFluidContainerTransfer transfer, ResourceCondition... conditions) {
    addTransfer(ResourceLocation.fromNamespaceAndPath(modId, name), transfer, conditions);
  }

  /** Adds generic fill and empty for a container */
  protected void addFillEmpty(String prefix, ItemLike item, ItemLike container, Fluid fluid, TagKey<Fluid> tag, long amount, ResourceCondition... conditions) {
    addTransfer(prefix + "empty",  new EmptyFluidContainerTransfer(Ingredient.of(item), ItemOutput.fromItem(container), new FluidStack(fluid, amount)), conditions);
    addTransfer(prefix + "fill", new FillFluidContainerTransfer(Ingredient.of(container), ItemOutput.fromItem(item), FluidIngredient.of(tag, amount)), conditions);
  }

  /** Adds generic fill and empty for a container */
  protected void addFillEmptyNBT(String prefix, ItemLike item, ItemLike container, Fluid fluid, TagKey<Fluid> tag, long amount, ResourceCondition... conditions) {
    addTransfer(prefix + "empty",  new EmptyFluidWithNBTTransfer(Ingredient.of(item), ItemOutput.fromItem(container), new FluidStack(fluid, amount)), conditions);
    addTransfer(prefix + "fill", new FillFluidWithNBTTransfer(Ingredient.of(container), ItemOutput.fromItem(item), FluidIngredient.of(tag, amount)), conditions);
  }

  @Override
  public CompletableFuture<?> run(CachedOutput cache) {
    addTransfers();
    allTransfers.forEach((id, data) -> saveJson(cache, id, data.toJson()));
  }

  /** Json with transfer and condition */
  private record TransferJson(IFluidContainerTransfer transfer, ResourceCondition[] conditions) {
    /** Serializes this to JSON */
    private JsonElement toJson() {
      JsonElement element = FluidContainerTransferManager.GSON.toJsonTree(transfer, IFluidContainerTransfer.class);
      assert element.isJsonObject();
      if (conditions.length != 0) {
        JsonArray array = new JsonArray();
        for (ResourceCondition condition : conditions) {
          array.add(condition.toJson());
        }
        element.getAsJsonObject().add(ResourceConditions.CONDITIONS_KEY, array);
      }
      return element;
    }
  }
}
