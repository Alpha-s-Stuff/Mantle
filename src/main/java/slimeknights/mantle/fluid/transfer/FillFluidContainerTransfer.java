package slimeknights.mantle.fluid.transfer;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import io.github.fabricators_of_create.porting_lib.fluids.FluidStack;
import io.github.fabricators_of_create.porting_lib.transfer.TransferUtil;
import lombok.RequiredArgsConstructor;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.apache.commons.lang3.function.TriFunction;
import org.jetbrains.annotations.Nullable;
import slimeknights.mantle.Mantle;
import slimeknights.mantle.recipe.helper.ItemOutput;
import slimeknights.mantle.recipe.ingredient.FluidIngredient;
import slimeknights.mantle.util.JsonHelper;

import java.lang.reflect.Type;
import java.util.function.Consumer;

/** Fluid transfer info that fills a fluid into an item */
@RequiredArgsConstructor
public class FillFluidContainerTransfer implements IFluidContainerTransfer {
  public static final ResourceLocation ID = Mantle.getResource("fill_item");

  private final Ingredient input;
  private final ItemOutput filled;
  private final FluidIngredient fluid;

  @Override
  public void addRepresentativeItems(Consumer<Item> consumer) {
    for (ItemStack stack : input.getItems()) {
      consumer.accept(stack.getItem());
    }
  }

  @Override
  public boolean matches(ItemStack stack, FluidStack fluid) {
    return input.test(stack) && this.fluid.test(fluid);
  }

  /** Gets the output filled with the given fluid */
  protected ItemStack getFilled(FluidStack drained) {
    return this.filled.get().copy();
  }

  @Nullable
  @Override
  public TransferResult transfer(ItemStack stack, FluidStack fluid, Storage<FluidVariant> handler) {
    long amount = this.fluid.getAmount(fluid.getFluid());
    FluidStack toDrain = new FluidStack(fluid, amount);
    long simulated = handler.simulateExtract(toDrain.getType(), toDrain.getAmount(), null);
    if (simulated == amount) {
      try (Transaction t = TransferUtil.getTransaction()) {
        long actual = handler.extract(toDrain.getType(), toDrain.getAmount(), t);
        if (actual != amount) {
          Mantle.logger.error("Wrong amount drained from {}, expected {}, filled {}", BuiltInRegistries.ITEM.getKey(stack.getItem()), fluid.getAmount(), actual);
        }
        t.commit();
      }
      return new TransferResult(getFilled(toDrain), toDrain, true);
    }
    return null;
  }

  @Override
  public JsonObject serialize(JsonSerializationContext context) {
    JsonObject json = new JsonObject();
    json.addProperty("type", ID.toString());
    json.add("input", input.toJson());
    json.add("filled", filled.serialize());
    json.add("fluid", fluid.serialize());
    return json;
  }

  /**
   * Unique loader instance
   */
  public static final JsonDeserializer<FillFluidContainerTransfer> DESERIALIZER = new Deserializer<>(FillFluidContainerTransfer::new);

  public record Deserializer<T extends FillFluidContainerTransfer>(TriFunction<Ingredient, ItemOutput, FluidIngredient, T> factory) implements JsonDeserializer<T> {
    @Override
    public T deserialize(JsonElement element, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
      JsonObject json = element.getAsJsonObject();
      Ingredient input = Ingredient.fromJson(JsonHelper.getElement(json, "input"));
      ItemOutput filled = ItemOutput.fromJson(JsonHelper.getElement(json, "filled"));
      FluidIngredient fluid = FluidIngredient.deserialize(json, "fluid");
      return factory.apply(input, filled, fluid);
    }
  }
}
