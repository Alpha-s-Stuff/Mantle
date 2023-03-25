package slimeknights.mantle.client.model.fluid;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.datafixers.util.Pair;
import io.github.fabricators_of_create.porting_lib.models.geometry.IGeometryLoader;
import io.github.fabricators_of_create.porting_lib.models.geometry.IUnbakedGeometry;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.fabricmc.fabric.api.renderer.v1.model.ForwardingBakedModel;
import net.fabricmc.fabric.api.resource.SimpleResourceReloadListener;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.SimpleBakedModel;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.material.Fluid;
import slimeknights.mantle.Mantle;
import slimeknights.mantle.client.model.util.ModelHelper;
import slimeknights.mantle.registration.ModelFluidAttributes;
import slimeknights.mantle.registration.ModelFluidAttributes.IFluidModelProvider;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/** Fluid model that allows a resource pack to control the textures of a block. Use alongside {@link ModelFluidAttributes} */
@RequiredArgsConstructor
public class FluidTextureModel implements IUnbakedGeometry<FluidTextureModel> {
  public static Loader LOADER = new Loader();

  private final int color;

  /** Checks if a texture is missing */
  private static boolean isMissing(Material material) {
    return MissingTextureAtlasSprite.getLocation().equals(material.texture());
  }

  /** Gets the texture, or null if missing */
  private static void getTexture(BlockModel owner, String name, Collection<Material> textures, Set<Pair<String,String>> missingTextureErrors) {
    Material material = owner.getMaterial(name);
    if (isMissing(material)) {
      missingTextureErrors.add(Pair.of(name, owner.name));
    }
    textures.add(material);
  }

//  @Override
//  public Collection<Material> getMaterials(IGeometryBakingContext owner, Function<ResourceLocation,UnbakedModel> modelGetter, Set<Pair<String,String>> missingTextureErrors) {
//    Set<Material> textures = new HashSet<>();
//    getTexture(owner, "still", textures, missingTextureErrors);
//    getTexture(owner, "flowing", textures, missingTextureErrors);
//    Material overlay = owner.getMaterial("overlay");
//    if (!isMissing(overlay)) {
//      textures.add(overlay);
//    }
//    return textures;
//  }

  @Override
  public BakedModel bake(BlockModel owner, ModelBaker baker, Function<Material,TextureAtlasSprite> spriteGetter, ModelState modelTransform, ItemOverrides overrides, ResourceLocation modelLocation) {
    Material still = owner.getMaterial("still");
    Material flowing = owner.getMaterial("flowing");
    Material overlay = owner.getMaterial("overlay");
    ResourceLocation overlayLocation = isMissing(overlay) ? null : overlay.texture();
    BakedModel baked = new SimpleBakedModel.Builder(owner.hasAmbientOcclusion(), owner.getGuiLight().lightLikeBlock(), true, owner.getTransforms(), overrides).particle(spriteGetter.apply(still)).build();
    return new Baked(baked, still.texture(), flowing.texture(), overlayLocation, color);
  }

  /** Data holder class, has no quads */
  private static class Baked extends ForwardingBakedModel {
    @Getter
    private final ResourceLocation still;
    @Getter
    private final ResourceLocation flowing;
    @Getter
    private final ResourceLocation overlay;
    @Getter
    private final int color;
    public Baked(BakedModel originalModel, ResourceLocation still, ResourceLocation flowing, @Nullable ResourceLocation overlay, int color) {
      wrapped = originalModel;
      this.still = still;
      this.flowing = flowing;
      this.overlay = overlay;
      this.color = color;
    }
  }

  /** Model loader, also doubles as the fluid model provider */
  private static class Loader implements IGeometryLoader<FluidTextureModel>, IFluidModelProvider, SimpleSynchronousResourceReloadListener {
    private final Map<Fluid,Baked> modelCache = new ConcurrentHashMap<>();

    /** Gets a model for a fluid */
    @Nullable
    private Baked getFluidModel(Fluid fluid) {
      return ModelHelper.getBakedModel(fluid.defaultFluidState().createLegacyBlock(), Baked.class);
    }

    /** Gets a model for a fluid from the cache */
    @Nullable
    private Baked getCachedModel(Fluid fluid) {
      return modelCache.computeIfAbsent(fluid, this::getFluidModel);
    }

    @Override
    @Nullable
    public ResourceLocation getStillTexture(Fluid fluid) {
      Baked model = getCachedModel(fluid);
      return model == null ? null : model.getStill();
    }

    @Override
    @Nullable
    public ResourceLocation getFlowingTexture(Fluid fluid) {
      Baked model = getCachedModel(fluid);
      return model == null ? null : model.getFlowing();
    }

    @Override
    @Nullable
    public ResourceLocation getOverlayTexture(Fluid fluid) {
      Baked model = getCachedModel(fluid);
      return model == null ? null : model.getOverlay();
    }

    @Override
    public int getColor(Fluid fluid) {
      Baked model = getCachedModel(fluid);
      return model == null ? -1 : model.getColor();
    }

    @Override
    public void onResourceManagerReload(ResourceManager resourceManager) {
      modelCache.clear();
    }

    @Override
    public FluidTextureModel read(JsonObject modelContents, JsonDeserializationContext deserializationContext) {
      int color = -1;
      if (modelContents.has("color")) {
        String colorString = GsonHelper.getAsString(modelContents, "color");
        int length = colorString.length();
        // prevent some invalid strings, colors should all be 6 or 8 digits
        if (colorString.charAt(0) == '-' || (length != 6 && length != 8)) {
          throw new JsonSyntaxException("Invalid color '" + colorString + "'");
        }
        try {
          color = (int)Long.parseLong(colorString, 16);
          // for 6 length, make fully opaque
          if (length == 6) {
            color |= 0xFF000000;
          }
        } catch (NumberFormatException e) {
          throw new JsonSyntaxException("Invalid color '" + colorString + "'");
        }
      }
      return new FluidTextureModel(color);
    }

    @Override
    public ResourceLocation getFabricId() {
      return Mantle.getResource("fluid_texture_model_loader");
    }
  }
}
