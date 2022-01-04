package slimeknights.mantle.lib.crafting;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import twilightforest.lib.extensions.IIngredientEx;
import java.util.stream.Stream;

import javax.annotation.Nullable;

import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public class NBTIngredient extends Ingredient implements IIngredientEx {

    private final ItemStack stack;

    protected NBTIngredient(ItemStack stack)
    {
        super(Stream.of(new ItemValue(stack)));
        this.stack = stack;
    }

    @Override
    public boolean test(@Nullable ItemStack input)
    {
        if (input == null)
            return false;
        //Can't use areItemStacksEqualUsingNBTShareTag because it compares stack size as well
        return this.stack.getItem() == input.getItem() && this.stack.getDamageValue() == input.getDamageValue() && CraftingHelper.areShareTagsEqual(this.stack, input);
    }

    @Override
    public boolean isSimple()
    {
        return false;
    }

    @Override
    public IIngredientSerializer<? extends Ingredient> getSerializer()
    {
        return Serializer.INSTANCE;
    }

    @Override
    public JsonElement toJson()
    {
        JsonObject json = new JsonObject();
        json.addProperty("type", CraftingHelper.getID(Serializer.INSTANCE).toString());
        json.addProperty("item", Registry.ITEM.getKey(stack.getItem()).toString());
        json.addProperty("count", stack.getCount());
        if (stack.hasTag())
            json.addProperty("nbt", stack.getTag().toString());
        return json;
    }

    public static class Serializer implements IIngredientSerializer<NBTIngredient>
    {
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
