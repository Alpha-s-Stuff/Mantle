package slimeknights.mantle.lib.mixin.client;

import com.mojang.blaze3d.vertex.VertexFormat;
import it.unimi.dsi.fastutil.ints.IntList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import slimeknights.mantle.lib.extensions.VertexFormatExtensions;

@Mixin(VertexFormat.class)
public class VertexFormatMixin implements VertexFormatExtensions {

  @Shadow
  @Final
  private IntList offsets;

  @Unique
  @Override
  public int getOffset(int index) { return offsets.getInt(index); }
}
