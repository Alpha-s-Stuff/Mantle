//package slimeknights.mantle.client.model.inventory;
//
//import com.google.gson.JsonDeserializationContext;
//import com.google.gson.JsonObject;
//import com.mojang.datafixers.util.Pair;
//import lombok.AllArgsConstructor;
//import lombok.Getter;
//import net.fabricmc.fabric.api.renderer.v1.model.ForwardingBakedModel;
//import net.minecraft.client.renderer.block.model.BlockModel;
//import net.minecraft.client.renderer.block.model.ItemOverrides;
//import net.minecraft.client.renderer.texture.TextureAtlasSprite;
//import net.minecraft.client.resources.model.BakedModel;
//import net.minecraft.client.resources.model.Material;
//import net.minecraft.client.resources.model.ModelBakery;
//import net.minecraft.client.resources.model.ModelState;
//import net.minecraft.client.resources.model.UnbakedModel;
//import net.minecraft.resources.ResourceLocation;
//import net.minecraft.server.packs.resources.ResourceManager;
//import slimeknights.mantle.client.model.util.SimpleBlockModel;
//import slimeknights.mantle.lib.model.IModelLoader;
//
//import java.util.Collection;
//import java.util.List;
//import java.util.Set;
//import java.util.function.Function;
//
///**
// * This model contains a list of multiple items to display in a TESR
// */
//@AllArgsConstructor
//public class InventoryModel implements UnbakedModel<InventoryModel> {
//  protected final SimpleBlockModel model;
//  protected final List<ModelItem> items;
//  protected final BlockModel owner;
//
//  @Override
//  public Collection<Material> getTextures(BlockModel owner, Function<ResourceLocation,UnbakedModel> modelGetter, Set<Pair<String,String>> missingTextureErrors) {
//    return model.getTextures(owner, modelGetter, missingTextureErrors);
//  }
//
//  @Override
//  public BakedModel bake(ModelBakery bakery, Function<Material,TextureAtlasSprite> spriteGetter, ModelState transform, ResourceLocation location) {
//    BakedModel baked = model.bakeModel(owner, transform, ItemOverrides.EMPTY, spriteGetter, location);
//    return new Baked(baked, items);
//  }
//
//  /** Baked model, mostly a data wrapper around a normal model */
//  @SuppressWarnings("WeakerAccess")
//  public static class Baked extends ForwardingBakedModel {
//    @Getter
//    private final List<ModelItem> items;
//    public Baked(BakedModel originalModel, List<ModelItem> items) {
//      this.wrapped = originalModel;
//      this.items = items;
//    }
//  }
//
//  /** Loader for this model */
//  public static class Loader extends IModelLoader<InventoryModel> {
//    /**
//     * Shared loader instance
//     */
//    public static final Loader INSTANCE = new Loader();
//
//    @Override
//    public void onResourceManagerReload(ResourceManager resourceManager) {}
//
//    @Override
//    public InventoryModel read(JsonDeserializationContext deserializationContext, JsonObject modelContents) {
//      SimpleBlockModel model = SimpleBlockModel.deserialize(deserializationContext, modelContents);
//      List<ModelItem> items = ModelItem.listFromJson(modelContents, "items");
//      return new InventoryModel(model, items);
//    }
//  }
//}
