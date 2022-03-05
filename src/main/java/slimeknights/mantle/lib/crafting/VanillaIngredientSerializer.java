package slimeknights.mantle.lib.crafting;

import com.google.gson.JsonObject;
import java.util.stream.Stream;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import slimeknights.mantle.lib.mixin.IngredientAccessor;

public class VanillaIngredientSerializer implements IIngredientSerializer<Ingredient>
{
    public static final VanillaIngredientSerializer INSTANCE  = new VanillaIngredientSerializer();

    public Ingredient parse(FriendlyByteBuf buffer)
    {
        return IngredientAccessor.callFromValues(Stream.generate(() -> new Ingredient.ItemValue(buffer.readItem())).limit(buffer.readVarInt()));
    }

    public Ingredient parse(JsonObject json)
    {
        return IngredientAccessor.callFromValues(Stream.of(IngredientAccessor.callValueFromJson(json)));
    }

    public void write(FriendlyByteBuf buffer, Ingredient ingredient)
    {
        ItemStack[] items = ingredient.getItems();
        buffer.writeVarInt(items.length);

        for (ItemStack stack : items)
            buffer.writeItem(stack);
    }
}
