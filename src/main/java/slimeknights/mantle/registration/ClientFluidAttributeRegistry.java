package slimeknights.mantle.registration;

import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.minecraft.world.level.material.Fluid;
import slimeknights.mantle.fluid.attributes.FluidAttributes;

// This class only exists to bypass class loading
public class ClientFluidAttributeRegistry {
  public static void register(Fluid fluid, FluidAttributes attributes) {
    FluidRenderHandlerRegistry.INSTANCE.register(fluid, new FluidAttributeClientHandler(attributes));
  }

  public static void registerUpsideDown(Fluid still, Fluid flowing, FluidAttributes attributes) {
    FluidRenderHandlerRegistry.INSTANCE.register(still, flowing, new UpsideDownFluidAttributeClientHandler(attributes));
  }
}
