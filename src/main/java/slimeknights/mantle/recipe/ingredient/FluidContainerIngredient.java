package slimeknights.mantle.recipe.ingredient;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.fabricators_of_create.porting_lib.transfer.TransferUtil;
import io.github.fabricators_of_create.porting_lib.transfer.item.ItemHandlerHelper;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredient;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredientSerializer;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import slimeknights.mantle.Mantle;
import slimeknights.mantle.registration.object.FluidObject;
import slimeknights.mantle.util.JsonHelper;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/** Ingredient that matches a container of fluid */
@SuppressWarnings("unused")  // API
public class FluidContainerIngredient implements CustomIngredient {
  public static final ResourceLocation ID = Mantle.getResource("fluid_container");
  public static final Serializer SERIALIZER = new Serializer();

  /** Ingredient to use for matching */
  private final FluidIngredient fluidIngredient;
  /** Internal ingredient to display the ingredient recipe viewers */
  @Nullable
  private final Ingredient display;
  private List<ItemStack> displayStacks;
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
  public static FluidContainerIngredient fromFluid(FluidObject<?> fluid) {
    return fromIngredient(fluid.ingredient(FluidConstants.BUCKET), Ingredient.of(fluid));
  }

  @Override
  public boolean test(@Nullable ItemStack stack) {
    // first, must have a fluid capability
    return stack != null && !stack.isEmpty() && TransferUtil.getFluidContained(stack).flatMap(contained -> {
      // second, must contain enough fluid
      if (!contained.isEmpty() && fluidIngredient.getAmount(contained.getFluid()) == contained.getAmount() && fluidIngredient.test(contained.getFluid())) {
        // so far so good, from this point on we are forced to make copies as we need to try draining, so copy and fetch the copy's cap
        ItemStack copy = ItemHandlerHelper.copyStackWithSize(stack, 1);
        return Optional.of(ContainerItemContext.withInitial(copy));
      }
      return Optional.empty();
    }).filter(cap -> {
      Storage<FluidVariant> storage = cap.find(FluidStorage.ITEM);
      if (storage == null)
        return false;
      // alright, we know it has the fluid, the question is just whether draining the fluid will give us the desired result
      FluidVariant fluid = StorageUtil.findStoredResource(storage);
      long amount = fluidIngredient.getAmount(fluid.getFluid());
      try (Transaction tx = Transaction.openOuter()) {
        long drained = storage.extract(fluid, amount, tx);
        // we need an exact match, and we need the resulting container item to be the same as the item stack's container item
        return drained == amount && ItemStack.matches(stack.getRecipeRemainder(), cap.getItemVariant().toStack(TransferUtil.truncateLong(cap.getAmount())));
      }
    }).isPresent();
  }

  @Override
  public List<ItemStack> getMatchingStacks() {
    if (displayStacks == null) {
      // no container? unfortunately hard to display this recipe so show nothing
      if (display == null) {
        displayStacks = List.of();
      } else {
        displayStacks = Arrays.asList(display.getItems());
      }
    }
    return displayStacks;
  }

  @Override
  public boolean requiresTesting() {
    return true;
  }

//  @Override Fabric: I don't know if this is needed
  public boolean isEmpty() {
    return false;
  }

  @Override
  public CustomIngredientSerializer<? extends CustomIngredient> getSerializer() {
    return SERIALIZER;
  }

  /** Serializer logic */
  private static class Serializer implements CustomIngredientSerializer<FluidContainerIngredient> {
    @Override
    public FluidContainerIngredient read(JsonObject json) {
      FluidIngredient fluidIngredient;
      // if we have fluid and its not a primitive, then its nested
      if (json.has("fluid") && !json.get("fluid").isJsonPrimitive()) {
        fluidIngredient = FluidIngredient.LOADABLE.getIfPresent(json, "fluid");
      } else {
        fluidIngredient = FluidIngredient.LOADABLE.convert(json, "fluid");
      }
      Ingredient display = null;
      if (json.has("display")) {
        display = Ingredient.fromJson(JsonHelper.getElement(json, "display"));
      }
      return new FluidContainerIngredient(fluidIngredient, display);
    }

    @Override
    public FluidContainerIngredient read(FriendlyByteBuf buffer) {
      FluidIngredient fluidIngredient = FluidIngredient.LOADABLE.decode(buffer);
      Ingredient display = null;
      if (buffer.readBoolean()) {
        display = Ingredient.fromNetwork(buffer);
      }
      return new FluidContainerIngredient(fluidIngredient, display);
    }

    @Override
    public void write(JsonObject json, FluidContainerIngredient ingredient) {
      JsonElement element = ingredient.fluidIngredient.serialize();

      if (element.isJsonObject()) {
        element.getAsJsonObject().asMap().forEach(json::add);
      } else {
        json.add("fluid", element);
      }
      json.addProperty("type", ID.toString());
      if (ingredient.display != null) {
        json.add("display", ingredient.display.toJson());
      }
    }

    @Override
    public void write(FriendlyByteBuf buffer, FluidContainerIngredient ingredient) {
      FluidIngredient.LOADABLE.encode(buffer, ingredient.fluidIngredient);
      if (ingredient.display != null) {
        buffer.writeBoolean(true);
        ingredient.display.toNetwork(buffer);
      } else {
        buffer.writeBoolean(false);
      }
    }

    @Override
    public ResourceLocation getIdentifier() {
      return ID;
    }
  }
}
