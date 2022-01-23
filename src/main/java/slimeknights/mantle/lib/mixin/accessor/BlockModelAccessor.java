package slimeknights.mantle.lib.mixin.accessor;

import com.google.gson.Gson;
import com.mojang.datafixers.util.Either;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.BlockElementFace;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.resources.model.Material;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(BlockModel.class)
public interface BlockModelAccessor {

  @Accessor
  static Gson getGSON() {throw new UnsupportedOperationException();}

  @Invoker
  static BakedQuad callBakeFace(BlockElement part, BlockElementFace partFace, TextureAtlasSprite sprite, Direction direction, ModelState transform, ResourceLocation location) {throw new UnsupportedOperationException();}

  @Accessor
  Map<String, Either<Material, String>> getTextureMap();
}
