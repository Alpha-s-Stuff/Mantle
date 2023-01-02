package slimeknights.mantle.plugin.rei;

import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.screen.ExclusionZones;
import me.shedaniel.rei.api.client.registry.screen.ExclusionZonesProvider;
import slimeknights.mantle.client.screen.MultiModuleScreen;
import slimeknights.mantle.inventory.MultiModuleContainerMenu;

import java.util.Collection;
import java.util.stream.Collectors;

public class REIPlugin implements REIClientPlugin {

  @Override
  public void registerExclusionZones(ExclusionZones registration) {
    registration.register(MultiModuleScreen.class, new MultiModuleContainerHandler());
  }

  private static class MultiModuleContainerHandler<C extends MultiModuleContainerMenu<?>> implements ExclusionZonesProvider<MultiModuleScreen<C>> {
    @Override
    public Collection<Rectangle> provide(MultiModuleScreen<C> guiContainer) {
      return guiContainer.getModuleAreas().stream().map(rect2i -> {
        return new Rectangle(rect2i.getX(), rect2i.getY(), rect2i.getWidth(), rect2i.getHeight());
      }).collect(Collectors.toList());
    }
  }
}
