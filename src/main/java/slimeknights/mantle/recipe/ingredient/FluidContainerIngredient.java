package slimeknights.mantle.recipe.ingredient;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.fabricators_of_create.porting_lib.fluids.FluidStack;
import io.github.fabricators_of_create.porting_lib.transfer.TransferUtil;
import io.github.fabricators_of_create.porting_lib.transfer.item.ItemHandlerHelper;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredient;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredientSerializer;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.material.Fluid;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import slimeknights.mantle.Mantle;
import slimeknights.mantle.registration.object.FluidObject;
import slimeknights.mantle.util.JsonHelper;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

/** Ingredient that matches a container of fluid */
public class FluidContainerIngredient implements CustomIngredient {
  public static final ResourceLocation ID = Mantle.getResource("fluid_container");
  public static final Serializer SERIALIZER = new Serializer();

  /** Ingredient to use for matching */
  private final FluidIngredient fluidIngredient;
  /** Internal ingredient to display the ingredient recipe viewers */
  @Nullable
  private final Ingredient display;
  private ItemStack[] displayStacks;
  protected FluidContainerIngredient(FluidIngredient fluidIngredient, @Nullable Ingredient display) {
    this.fluidIngredient = fluidIngredient;
    this.display = display;
  }

  /** Creates an instance from a fluid ingredient with a display container */
  public static FluidContainerIngredient fromIngredient(FluidIngredient ingredient, Ingredient display) {
    return new FluidContainerIngredient(ingredient, display);
  }

  /** Creates an instance from a fluid ingredient with no display, not recommended */
  public static FluidContainerIngredient fromIngredient(FluidIngredient ingredient) {
    return new FluidContainerIngredient(ingredient, null);
  }

  /** Creates an instance from a fluid ingredient with a display container */
  public static FluidContainerIngredient fromFluid(FluidObject<?> fluid, boolean forgeTag) {
    return fromIngredient(FluidIngredient.of(forgeTag ? fluid.getForgeTag() : fluid.getLocalTag(), FluidConstants.BUCKET), Ingredient.of(fluid));
  }

  @Override
  public boolean test(@Nullable ItemStack stack) {
    // first, must have a fluid capability
    return stack != null && !stack.isEmpty() && Optional.ofNullable(FluidStorage.ITEM.find(stack, ContainerItemContext.withInitial(stack))).flatMap(cap -> {
      // second, must contain enough fluid
      FluidStack contained = TransferUtil.getFirstFluid(cap);
      if (!contained.isEmpty() && fluidIngredient.getAmount(contained.getFluid()) == contained.getAmount() && fluidIngredient.test(contained.getFluid())) {
        // so far so good, from this point on we are forced to make copies as we need to try draining, so copy and fetch the copy's cap
        ItemStack copy = ItemHandlerHelper.copyStackWithSize(stack, 1);
        return Optional.ofNullable(copy);
      }
      return Optional.empty();
    }).filter(cap -> {
      // alright, we know it has the fluid, the question is just whether draining the fluid will give us the desired result
      Storage<FluidVariant> storage = FluidStorage.ITEM.find(cap, ContainerItemContext.withInitial(cap));
      Fluid fluid = TransferUtil.getFirstFluid(storage).getFluid();
      long amount = fluidIngredient.getAmount(fluid);
      FluidStack drained = TransferUtil.extractAnyFluid(storage, amount);
      // we need an exact match, and we need the resulting container item to be the same as the item stack's container item
      return drained.getFluid() == fluid && drained.getAmount() == amount && ItemStack.matches(stack.getItem().getCraftingRemainingItem().getDefaultInstance(), cap);
    }).isPresent();
  }

  @Override
  public boolean requiresTesting() {
    return true;
  }

  @Override
  public List<ItemStack> getMatchingStacks() {
    if (displayStacks == null) {
      // no container? unfortunately hard to display this recipe so show nothing
      if (display == null) {
        displayStacks = new ItemStack[0];
      } else {
        displayStacks = display.getItems();
      }
    }
    return List.of(displayStacks);
  }



  @Override
  public Serializer getSerializer() {
    return SERIALIZER;
  }

  /** Serializer logic */
  private static class Serializer implements CustomIngredientSerializer<FluidContainerIngredient> {
    @Override
    public ResourceLocation getIdentifier() {
      return ID;
    }

    @Override
    public void write(JsonObject jsonObject, FluidContainerIngredient ingredient) {
      JsonElement element = ingredient.fluidIngredient.serialize();
      JsonObject json;
      if (element.isJsonObject()) {
        json = element.getAsJsonObject();
      } else {
        json = new JsonObject();
        json.add("fluid", element);
      }
      json.addProperty("type", ID.toString());
      if (ingredient.display != null) {
        json.add("display", ingredient.display.toJson());
      }
      jsonObject.add("fluid", json);
    }

    @Override
    public void write(FriendlyByteBuf buffer, FluidContainerIngredient ingredient) {
      ingredient.fluidIngredient.write(buffer);
      if (ingredient.display != null) {
        buffer.writeBoolean(true);
        ingredient.display.toNetwork(buffer);
      } else {
        buffer.writeBoolean(false);
      }
    }

    @Override
    public FluidContainerIngredient read(JsonObject json) {
      json = json.getAsJsonObject("fluid");
      FluidIngredient fluidIngredient;
      // if we have fluid, its a nested ingredient. Otherwise this object itself is the ingredient
      if (json.has("fluid")) {
        fluidIngredient = FluidIngredient.deserialize(json, "fluid");
      } else {
        fluidIngredient = FluidIngredient.deserialize((JsonElement) json, "fluid");
      }
      Ingredient display = null;
      if (json.has("display")) {
        display = Ingredient.fromJson(JsonHelper.getElement(json, "display"));
      }
      return new FluidContainerIngredient(fluidIngredient, display);
    }

    @Override
    public FluidContainerIngredient read(FriendlyByteBuf buffer) {
      FluidIngredient fluidIngredient = FluidIngredient.read(buffer);
      Ingredient display = null;
      if (buffer.readBoolean()) {
        display = Ingredient.fromNetwork(buffer);
      }
      return new FluidContainerIngredient(fluidIngredient, display);
    }
  }
}
