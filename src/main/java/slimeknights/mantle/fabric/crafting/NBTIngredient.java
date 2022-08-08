package slimeknights.mantle.fabric.crafting;

import java.util.stream.Stream;

import javax.annotation.Nullable;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import io.github.fabricators_of_create.porting_lib.crafting.PartialNBTIngredient;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.network.FriendlyByteBuf;

/** Ingredient that matches the given stack, performing an exact NBT match. Use {@link PartialNBTIngredient} if you need partial match. */
public class NBTIngredient extends AbstractIngredient {
  private final ItemStack stack;
  protected NBTIngredient(ItemStack stack) {
    super(Stream.of(new Ingredient.ItemValue(stack)));
    this.stack = stack;
  }

  /** Creates a new ingredient matching the given stack and tag */
  public static NBTIngredient of(ItemStack stack) {
    return new NBTIngredient(stack);
  }

  @Override
  public boolean test(@Nullable ItemStack input) {
    if (input == null)
      return false;
    //Can't use areItemStacksEqualUsingNBTShareTag because it compares stack size as well
    return this.stack.getItem() == input.getItem() && this.stack.getDamageValue() == input.getDamageValue() && CraftingHelper.areShareTagsEqual(this.stack, input);
  }

  @Override
  public boolean isSimple() {
    return false;
  }

  @Override
  public IIngredientSerializer<? extends Ingredient> getSerializer() {
    return Serializer.INSTANCE;
  }

  @Override
  public JsonElement toJson() {
    JsonObject json = new JsonObject();
    json.addProperty("type", CraftingHelper.getID(Serializer.INSTANCE).toString());
    json.addProperty("item", stack.getItem().getRegistryName().toString());
    json.addProperty("count", stack.getCount());
    if (stack.hasTag())
      json.addProperty("nbt", stack.getTag().toString());
    return json;
  }

  public static class Serializer implements IIngredientSerializer<NBTIngredient> {
    public static final Serializer INSTANCE = new Serializer();

    @Override
    public NBTIngredient parse(FriendlyByteBuf buffer) {
      return new NBTIngredient(buffer.readItem());
    }

    @Override
    public NBTIngredient parse(JsonObject json) {
      return new NBTIngredient(CraftingHelper.getItemStack(json, true));
    }

    @Override
    public void write(FriendlyByteBuf buffer, NBTIngredient ingredient) {
      buffer.writeItem(ingredient.stack);
    }
  }
}
