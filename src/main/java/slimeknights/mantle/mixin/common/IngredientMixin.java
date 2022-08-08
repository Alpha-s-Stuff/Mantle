package slimeknights.mantle.mixin.common;

import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Ingredient.Value;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import slimeknights.mantle.fabric.crafting.CraftingHelper;
import slimeknights.mantle.fabric.crafting.IIngredientSerializer;
import slimeknights.mantle.fabric.crafting.IngredientExtensions;
import slimeknights.mantle.fabric.crafting.VanillaIngredientSerializer;

import java.util.Arrays;
import java.util.stream.Stream;

@Mixin(value = Ingredient.class, priority = 2340) // magic random number to make sure we apply in the correct order
public abstract class IngredientMixin implements IngredientExtensions {

  @Shadow
  private
  ItemStack[] itemStacks;

  @Shadow
  private IntList stackingIds;

  @Shadow
  @Final
  public static Ingredient EMPTY;

  @Shadow
  @Final
  private Value[] values;

  @Unique
  private boolean port_lib$simple;
  @Unique
  private final boolean port_lib$vanilla = this.getClass().equals(Ingredient.class);

  @Inject(method = "<init>", at = @At("TAIL"))
  private void port_lib$init(Stream<? extends Ingredient.Value> stream, CallbackInfo ci) {
    port_lib$simple = Arrays.stream(values).noneMatch(list -> list.getItems().stream().anyMatch(stack -> stack.getItem().canBeDepleted()));
  }

  @Inject(method = "toNetwork", at = @At(value = "INVOKE", shift = Shift.AFTER, target = "Lnet/minecraft/world/item/crafting/Ingredient;dissolve()V"), cancellable = true)
  private void port_lib$toNetwork(FriendlyByteBuf buffer, CallbackInfo ci) {
    buffer.writeBoolean(port_lib$vanilla);
    if (!port_lib$vanilla) {
      CraftingHelper.write(buffer, (Ingredient) (Object) this);
      ci.cancel();
    }
  }

  @Inject(method = "fromNetwork", at = @At("HEAD"), cancellable = true)
  private static void port_lib$fromNetwork(FriendlyByteBuf buffer, CallbackInfoReturnable<Ingredient> cir) {
    boolean vanilla = buffer.readBoolean();
    if (!vanilla)
      cir.setReturnValue(CraftingHelper.getIngredient(buffer.readResourceLocation(), buffer));
  }

  @Override
  public IIngredientSerializer<? extends Ingredient> getSerializer() {
    if (!port_lib$vanilla) throw new IllegalStateException("Modders must implement Ingredient.getSerializer in their custom Ingredients: " + this);
    return VanillaIngredientSerializer.INSTANCE;
  }

  @Override
  public boolean isSimple() {
    return (Object) this == EMPTY || port_lib$simple;
  }

  @Override
  public void invalidate() {
    itemStacks = null;
    stackingIds = null;
  }
}
