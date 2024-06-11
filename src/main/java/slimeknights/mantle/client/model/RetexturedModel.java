package slimeknights.mantle.client.model;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.datafixers.util.Either;
import io.github.fabricators_of_create.porting_lib.models.CustomParticleIconModel;
import io.github.fabricators_of_create.porting_lib.models.geometry.IGeometryLoader;
import io.github.fabricators_of_create.porting_lib.models.geometry.IUnbakedGeometry;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import net.minecraftforge.client.model.geometry.IGeometryLoader;
import net.minecraftforge.client.model.geometry.IUnbakedGeometry;
import slimeknights.mantle.client.model.util.ColoredBlockModel;
import slimeknights.mantle.client.model.util.DynamicBakedWrapper;
import slimeknights.mantle.client.model.util.GeometryContextWrapper;
import slimeknights.mantle.client.model.util.ModelHelper;
import slimeknights.mantle.client.model.util.ModelTextureIteratable;
import slimeknights.mantle.client.model.util.SimpleBlockModel;
import slimeknights.mantle.item.RetexturedBlockItem;
import slimeknights.mantle.util.RetexturedHelper;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Model that dynamically retextures a list of textures based on data from {@link RetexturedHelper}.
 */
@SuppressWarnings("WeakerAccess")
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class RetexturedModel implements IUnbakedGeometry<RetexturedModel> {
  /** Loader instance */
  public static IGeometryLoader<RetexturedModel> LOADER = RetexturedModel::deserialize;

  private final SimpleBlockModel model;
  private final Set<String> retextured;

  @Override
  public void resolveParents(Function<ResourceLocation,UnbakedModel> modelGetter, BlockModel owner) {
    model.resolveParents(modelGetter, owner);
  }

  @Override
  public BakedModel bake(BlockModel owner, ModelBaker baker, Function<Material,TextureAtlasSprite> spriteGetter, ModelState transform, ItemOverrides overrides, ResourceLocation location, boolean isGui3d) {
    // bake the model and return
    BakedModel baked = model.bake(owner, baker, spriteGetter, transform, overrides, location, isGui3d);
    return new Baked(baked, owner, model, transform, getAllRetextured(owner, this.model, retextured));
  }

  /**
   * Gets a list of all names to retexture based on the block model texture references
   * @param owner        Model config instance
   * @param model        Model fallback
   * @param originalSet  Original list of names to retexture
   * @return  Set of textures including parent textures
   */
  public static Set<String> getAllRetextured(BlockModel owner, SimpleBlockModel model, Set<String> originalSet) {
    Set<String> retextured = Sets.newHashSet(originalSet);
    for (Map<String,Either<Material, String>> textures : ModelTextureIteratable.of(owner, model)) {
      textures.forEach((name, either) ->
        either.ifRight(parent -> {
          if (retextured.contains(parent)) {
            retextured.add(name);
          }
        })
      );
    }
    return ImmutableSet.copyOf(retextured);
  }

  /** Deserializes a retextured model from JSON */
  public static RetexturedModel deserialize(JsonObject json, JsonDeserializationContext context) {
    // get base model
    ColoredBlockModel model = ColoredBlockModel.deserialize(json, context);
    // get list of textures to retexture
    Set<String> retextured = getRetexturedNames(json);
    // return retextured model
    return new RetexturedModel(model, retextured);
  }

  /**
   * Gets the list of retextured textures from the model
   * @param json  Model json
   * @return  List of textures
   */
  public static Set<String> getRetexturedNames(JsonObject json) {
    if (json.has("retextured")) {
      // if an array, set from each texture in array
      JsonElement retextured = json.get("retextured");
      if (retextured.isJsonArray()) {
        JsonArray array = retextured.getAsJsonArray();
        if (array.size() == 0) {
          throw new JsonSyntaxException("Must have at least one texture in retextured");
        }
        ImmutableSet.Builder<String> builder = ImmutableSet.builder();
        for (int i = 0; i < array.size(); i++) {
          builder.add(GsonHelper.convertToString(array.get(i), "retextured[" + i + "]"));
        }
        return builder.build();
      }
      // if string, single texture
      if (retextured.isJsonPrimitive()) {
        return ImmutableSet.of(retextured.getAsString());
      }
    }
    // if neither or missing, error
    throw new JsonSyntaxException("Missing retextured, expected to find a String or a JsonArray");
  }

  /** Baked variant of the model, used to swap out quads based on the texture */
  public static class Baked extends DynamicBakedWrapper<BakedModel> implements CustomParticleIconModel {
    /** Cache of texture name to baked model */
    private final Map<ResourceLocation,BakedModel> cache = new ConcurrentHashMap<>();
    /* Properties for rebaking */
    private final BlockModel owner;
    private final SimpleBlockModel model;
    private final ModelState transform;
    /** List of texture names that are retextured */
    private final Set<String> retextured;

    public Baked(BakedModel baked, BlockModel owner, SimpleBlockModel model, ModelState transform, Set<String> retextured) {
      super(baked);
      this.model = model;
      this.owner = owner;
      this.transform = transform;
      this.retextured = retextured;
    }

    /**
     * Gets the model with the given texture applied
     * @param name  Texture location
     * @return  Retextured model
     */
    private BakedModel getRetexturedModel(ResourceLocation name) {
      return model.bakeDynamic(new RetexturedContext(owner, retextured, name), transform);
    }

    /**
     * Gets a cached retextured model, computing it if missing from the cache
     * @param block  Block determining the texture
     * @return  Retextured model
     */
    private BakedModel getCachedModel(Block block) {
      return cache.computeIfAbsent(ModelHelper.getParticleTexture(block), this::getRetexturedModel);
    }

    @Override
    public TextureAtlasSprite getParticleIcon(Object obj) {
      // if particle is retextured, fetch particle from the cached model
      if (retextured.contains("particle") && obj instanceof IModelData data) {
        Block block = data.get(RetexturedHelper.BLOCK_PROPERTY);
        if (block != null) {
          var cached = getCachedModel(block);
          return cached instanceof CustomParticleIconModel cachedParticle ? cachedParticle.getParticleIcon(data) : cached.getParticleIcon();
        }
      }
      if (wrapped instanceof CustomParticleIconModel customParticleIconModel)
        return customParticleIconModel.getParticleIcon(obj);
      return wrapped.getParticleIcon();
    }

    @Override
    public void emitBlockQuads(BlockAndTintGetter blockView, BlockState state, BlockPos pos, Supplier<RandomSource> randomSupplier, RenderContext context) {
      if(blockView.getBlockEntityRenderData(pos) instanceof IModelData data) {
        Block block = data.getData(RetexturedHelper.BLOCK_PROPERTY);
        if (block == null) {
          super.emitBlockQuads(blockView, state, pos, randomSupplier, context);
          return;
        }
        getCachedModel(block).emitBlockQuads(blockView, state, pos, randomSupplier, context);
      } else {
        super.emitBlockQuads(blockView, state, pos, randomSupplier, context);
      }
    }

    @Override
    public void emitItemQuads(ItemStack stack, Supplier<RandomSource> randomSupplier, RenderContext context) {
      if (stack.isEmpty() || !stack.hasTag()) {
        super.emitItemQuads(stack, randomSupplier, context);
        return;
      }

      // get the block first, ensuring its valid
      Block block = RetexturedBlockItem.getTexture(stack);
      if (block == Blocks.AIR) {
        super.emitItemQuads(stack, randomSupplier, context);
        return;
      }

      // if valid, use the block
      getCachedModel(block).emitItemQuads(stack, randomSupplier, context);
    }

    @Override
    public ItemOverrides getOverrides() {
      return RetexturedOverride.INSTANCE;
    }
  }

  /**
   * Model configuration wrapper to retexture the block
   */
  public static class RetexturedContext extends GeometryContextWrapper {
    /** List of textures to retexture */
    private final Set<String> retextured;
    /** Replacement texture */
    private final Material texture;

    /**
     * Creates a new configuration wrapper
     * @param base        Original model configuration
     * @param retextured  Set of textures that should be retextured
     * @param texture     New texture to replace those in the set
     */
    public RetexturedContext(BlockModel base, Set<String> retextured, ResourceLocation texture) {
      super(base);
      this.retextured = retextured;
      this.texture = new Material(InventoryMenu.BLOCK_ATLAS, texture);
    }

    @Override
    public boolean hasMaterial(String name) {
      if (retextured.contains(name)) {
        return !MissingTextureAtlasSprite.getLocation().equals(texture.texture());
      }
      return super.hasMaterial(name);
    }

    @Override
    public Material getMaterial(String name) {
      if (retextured.contains(name)) {
        return texture;
      }
      return super.getMaterial(name);
    }
  }

  /** Override list to swap the texture in from NBT */
  private static class RetexturedOverride extends ItemOverrides {
    private static final RetexturedOverride INSTANCE = new RetexturedOverride();

    @Nullable
    @Override
    public BakedModel resolve(BakedModel originalModel, ItemStack stack, @Nullable ClientLevel world, @Nullable LivingEntity entity, int pSeed) {
      if (stack.isEmpty() || !stack.hasTag()) {
        return originalModel;
      }

      // get the block first, ensuring its valid
      Block block = RetexturedBlockItem.getTexture(stack);
      if (block == Blocks.AIR) {
        return originalModel;
      }

      // if valid, use the block
      return ((Baked) ModelHelper.unwrap(originalModel, Baked.class)).getCachedModel(block);
    }
  }
}
