package slimeknights.mantle.fluid;

import io.github.fabricators_of_create.porting_lib.fluids.FluidType;
//import slimeknights.mantle.fluid.texture.ClientInvertedFluidType;

import java.util.function.Consumer;

/** Fluid type adding an extra flipped texture for the in world block */
public class InvertedFluidType extends FluidType {
  public InvertedFluidType(Properties properties) {
    super(properties);
  }

//  @Override
//  public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
//    consumer.accept(new ClientInvertedFluidType(this));
//  }
}
