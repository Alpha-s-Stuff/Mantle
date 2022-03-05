package slimeknights.mantle.lib.mixin.common;

import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import slimeknights.mantle.lib.crafting.CraftingHelper;
import slimeknights.mantle.lib.crafting.IIngredientSerializer;
import slimeknights.mantle.lib.crafting.VanillaIngredientSerializer;
import slimeknights.mantle.lib.extensions.IngredientEx;

import java.util.stream.Stream;

@Mixin(Ingredient.class)
public class IngredientMixin implements IngredientEx {

    @Shadow private ItemStack[] itemStacks;

    @Shadow private IntList stackingIds;

    @Shadow @Final public static Ingredient EMPTY;
    private static final java.util.Set<Ingredient> INSTANCES = java.util.Collections.newSetFromMap(new java.util.WeakHashMap<Ingredient, Boolean>());

    @Final @Unique
    public boolean isSimple;

    @Inject(method = "<init>", at = @At("TAIL"))
    public void init(Stream stream, CallbackInfo ci) {
//        INSTANCES.add((Ingredient) (Object) this);
    }

    @Inject(method = "toNetwork", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/crafting/Ingredient;dissolve()V"))
    private void toNetwork(FriendlyByteBuf buffer, CallbackInfo ci) {
        if (!this.isVanilla()) {
            CraftingHelper.write(buffer, (Ingredient) (Object) this);
            return;
        }
    }

    @Inject(method = "fromNetwork", at = @At("HEAD"), cancellable = true)
    private static void fromNetwork(FriendlyByteBuf buffer, CallbackInfoReturnable<Ingredient> cir) {
        int size = buffer.readVarInt();
        if (size == -1) cir.setReturnValue(CraftingHelper.getIngredient(buffer.readResourceLocation(), buffer));
    }

    @Override
    public IIngredientSerializer<? extends Ingredient> getSerializer() {
        if (!isVanilla()) throw new IllegalStateException("Modders must implement Ingredient.getSerializer in their custom Ingredients: " + this);
            return VanillaIngredientSerializer.INSTANCE;
    }

    public final boolean isVanilla() {
        return ((Class)this.getClass()) == Ingredient.class;
    }

    @Override
    public boolean isSimple() {
        return (Object) this == EMPTY;
    }

    @Override
    public void invalidate() {
        itemStacks = null;
        stackingIds = null;
    }
}
