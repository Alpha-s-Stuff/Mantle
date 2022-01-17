package slimeknights.mantle.lib.mixin.accessor;

import com.google.gson.Gson;
import com.mojang.datafixers.util.Either;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.resources.model.Material;

@Mixin(BlockModel.class)
public interface BlockModelAccessor {

  @Accessor
  static Gson getGSON() {throw new UnsupportedOperationException();}

  @Accessor
  Map<String, Either<Material, String>> getTextureMap();
}
