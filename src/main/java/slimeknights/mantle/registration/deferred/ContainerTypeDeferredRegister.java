package slimeknights.mantle.registration.deferred;

import net.minecraft.core.Registry;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import slimeknights.mantle.lib.util.RegistryObject;

/**
 * Deferred register for container types, automatically mapping a factory argument in {@link IForgeMenuType}
 */
@SuppressWarnings("unused")
public class ContainerTypeDeferredRegister extends DeferredRegisterWrapper<MenuType<?>> {

  public ContainerTypeDeferredRegister(String modID) {
    super(Registry.MENU, modID);
  }

  /**
   * Registers a container type
   * @param name     Container name
   * @param factory  Container factory
   * @param <C>      Container type
   * @return  Registry object containing the container type
   */
  public <C extends AbstractContainerMenu> RegistryObject<MenuType<C>> register(String name, MenuType.MenuSupplier<C> factory) {
    return register.register(name, () -> new MenuType(factory));
  }
}
