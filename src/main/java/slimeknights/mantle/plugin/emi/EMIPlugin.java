package slimeknights.mantle.plugin.emi;

import dev.emi.emi.api.EmiExclusionArea;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.widget.Bounds;
import net.minecraft.client.renderer.Rect2i;
import slimeknights.mantle.client.screen.MultiModuleScreen;
import slimeknights.mantle.inventory.MultiModuleContainerMenu;

import java.util.function.Consumer;

public class EMIPlugin implements EmiPlugin {

  @SuppressWarnings({"unchecked", "rawtypes"})
  @Override
  public void register(EmiRegistry emiRegistry) {
    emiRegistry.addExclusionArea(MultiModuleScreen.class, new MultiModuleContainerHandler());
  }

  private static class MultiModuleContainerHandler<C extends MultiModuleContainerMenu<?>> implements EmiExclusionArea<MultiModuleScreen<C>> {
    @Override
    public void addExclusionArea(MultiModuleScreen<C> screen, Consumer<Bounds> consumer) {
      for (Rect2i area : screen.getModuleAreas()) {
        consumer.accept(new Bounds(area.getX(), area.getY(), area.getWidth(), area.getHeight()));
      }
    }
  }
}
