package slimeknights.mantle.lib.extensions;

import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import slimeknights.mantle.lib.model.BlockModelConfiguration;

import java.util.function.Function;

public interface BlockModelExtensions {

  default BlockModelConfiguration getGeometry() {
	  throw new RuntimeException("I think your kinda bad, this shouldn't happen!");
  }

  default ItemOverrides getOverrides(ModelBakery pModelBakery, BlockModel pModel, Function<Material, TextureAtlasSprite> textureGetter) {
    throw new RuntimeException("I think your kinda bad, this shouldn't happen!");
  }
}
