package slimeknights.mantle.recipe.data;

import io.github.fabricators_of_create.porting_lib.fluids.FluidStack;
import lombok.RequiredArgsConstructor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import slimeknights.mantle.data.loadable.Loadable;
import slimeknights.mantle.data.loadable.Loadables;
import slimeknights.mantle.data.loadable.primitive.LongLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.recipe.ingredient.FluidIngredient;

import java.util.List;

/** Datagen fluid ingredient to create an ingredient matching a fluid from another mod, should not be used outside datagen */
@RequiredArgsConstructor(staticName = "of")
public class FluidNameIngredient extends FluidIngredient {
  private static final RecordLoadable<FluidNameIngredient> LOADABLE = RecordLoadable.create(
    Loadables.RESOURCE_LOCATION.requiredField("fluid", i -> i.fluidName),
    LongLoadable.FROM_ONE.requiredField("amount", i -> i.amount),
    FluidNameIngredient::new);

  private final ResourceLocation fluidName;
  private final long amount;

  @Override
  public Loadable<?> loadable() {
    return LOADABLE;
  }

  @Override
  public boolean test(Fluid fluid) {
    throw new UnsupportedOperationException();
  }

  @Override
  public long getAmount(Fluid fluid) {
    return amount;
  }

  @Override
  protected List<FluidStack> getAllFluids() {
    throw new UnsupportedOperationException();
  }
}
