package slimeknights.mantle.lib.mixin.accessor;

import com.mojang.datafixers.util.Either;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(BlockModel.Deserializer.class)
public interface DeserializerAccessor {

  @Invoker
  static Either<Material, String> callParseTextureLocationOrReference(ResourceLocation location, String name) {
    throw new UnsupportedOperationException();
  }
}
