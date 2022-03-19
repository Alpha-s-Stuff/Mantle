package slimeknights.mantle.registration;

import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import slimeknights.mantle.client.model.fluid.FluidTextureModel;
import io.github.fabricators_of_create.porting_lib.transfer.fluid.FluidAttributes;
import io.github.fabricators_of_create.porting_lib.util.EnvExecutor;
import slimeknights.mantle.client.render.MantleFluidRenderHandler;

import javax.annotation.Nullable;
import java.util.function.BiFunction;

/** Extension of fluid attributes that moves the fluid textures and color to the resource pack via {@link FluidTextureModel} (registered as loader <code>mantle:fluid_texture</code> for the fluid block ) */
public class ModelFluidAttributes extends FluidAttributes {
  public static final IFluidModelProvider MODEL_PROVIDER = EnvExecutor.unsafeRunForDist(() -> () -> FluidTextureModel.LOADER, () -> () -> IFluidModelProvider.EMPTY);

  /** Creates a new builder */
  public static FluidAttributes.Builder builder() {
    return new Builder(ModelFluidAttributes::new);
  }

  private final Fluid fluid;
  protected ModelFluidAttributes(FluidAttributes.Builder builder, Fluid fluid) {
    super(builder, fluid);
    this.fluid = fluid;
    EnvExecutor.runWhenOn(EnvType.CLIENT, () -> () -> {
      ClientSpriteRegistryCallback.event(TextureAtlas.LOCATION_BLOCKS).register((atlasTexture, registry) -> {
        registry.register(getStillTexture());
        registry.register(getFlowingTexture());
        registry.register(getStillTexture());
      });
      FluidRenderHandlerRegistry.INSTANCE.register(fluid, new MantleFluidRenderHandler(getStillTexture(), getFlowingTexture(), getOverlayTexture(), fluid));
    });
  }

  @Override
  public ResourceLocation getStillTexture() {
    ResourceLocation texture = MODEL_PROVIDER.getStillTexture(fluid);
    if (texture == null) {
      return super.getStillTexture();
    }
    return texture;
  }

  @Override
  public ResourceLocation getFlowingTexture() {
    ResourceLocation texture = MODEL_PROVIDER.getFlowingTexture(fluid);
    if (texture == null) {
      return super.getFlowingTexture();
    }
    return texture;
  }

  @Nullable
  @Override
  public ResourceLocation getOverlayTexture() {
    return MODEL_PROVIDER.getOverlayTexture(fluid);
  }

  @Override
  public int getColor() {
    return MODEL_PROVIDER.getColor(fluid);
  }

  /** Extension of {@link FluidAttributes.Builder} redirecting fluid textures and colors to the resource pack */
  public static class Builder extends FluidAttributes.Builder {
    private static final ResourceLocation FALLBACK_STILL = new ResourceLocation("block/water_still");
    private static final ResourceLocation FALLBACK_FLOWING = new ResourceLocation("block/water_flow");

    protected Builder(BiFunction<FluidAttributes.Builder,Fluid,FluidAttributes> factory) {
      super(FALLBACK_STILL, FALLBACK_FLOWING, factory);
    }
  }

  /** Interface to proxy the fluid model provider */
  public interface IFluidModelProvider {
    /** Serverside instance of the model provider, does nothing */
    IFluidModelProvider EMPTY = new IFluidModelProvider() {};

    /** Gets the still texture for a fluid */
    @Nullable
    default ResourceLocation getStillTexture(Fluid fluid) {
      return null;
    }

    /** Gets the flowing texture for a fluid */
    @Nullable
    default ResourceLocation getFlowingTexture(Fluid fluid) {
      return null;
    }

    /** Gets the overlay texture for a fluid */
    @Nullable
    default ResourceLocation getOverlayTexture(Fluid fluid) {
      return null;
    }

    /** Gets the color for a fluid */
    default int getColor(Fluid fluid) {
      return -1;
    }
  }
}
