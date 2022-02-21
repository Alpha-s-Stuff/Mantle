package slimeknights.mantle.registration.adapter;

import net.minecraft.core.Registry;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import slimeknights.mantle.Mantle;

@SuppressWarnings("unused")
public class ContainerTypeRegistryAdapter extends RegistryAdapter<MenuType<?>> {
  /** @inheritDoc */
  public ContainerTypeRegistryAdapter(String modId) {
    super(Registry.MENU, modId);
  }

  /** @inheritDoc */
  public ContainerTypeRegistryAdapter() {
    super(Registry.MENU, Mantle.modId);
  }

//  /**
//   * Registers a container type
//   * @param name     Container name
//   * @param factory  Container factory
//   * @param <C>      Container type
//   * @return  Registry object containing the container type
//   */
//  public <C extends AbstractContainerMenu> MenuType<C> registerType(IContainerFactory<C> factory, String name) {
//    return register(IForgeMenuType.create(factory), name);
//  }
}
