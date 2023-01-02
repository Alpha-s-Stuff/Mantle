package slimeknights.mantle.registration;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributeHandler;
import net.minecraft.resources.ResourceLocation;

public class FluidAttributeBuilder implements FluidVariantAttributeHandler {

  private int lightLevel;

  public FluidAttributeBuilder luminosity(int lightLevel) {
    this.lightLevel = lightLevel;
    return this;
  }

  public ResourceLocation getStillTexture() {
    return null;
  }

  public ResourceLocation getFlowingTexture() {
    return null;
  }

  public ResourceLocation getOverlayTexture() {
    return null;
  }

  public int getColor() {
    return 0;
  }

  @Override
  public int getLuminance(FluidVariant variant) {
    return lightLevel;
  }


}
