package slimeknights.mantle.lib.mixin.accessor;

import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.resources.model.Material;
import net.minecraft.world.level.block.state.properties.WoodType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Sheets.class)
public interface SheetsAccessor {

  @Invoker
  static Material callCreateSignMaterial(WoodType woodType) {
    throw new UnsupportedOperationException();
  }
}
