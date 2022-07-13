package slimeknights.mantle.registration.deferred;

import io.github.fabricators_of_create.porting_lib.util.RegistryObject;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.core.Registry;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

/**
 * Deferred register for menu types, automatically mapping a factory argument in {@link ExtendedScreenHandlerType.ExtendedFactory}
 */
@SuppressWarnings("unused")
public class MenuTypeDeferredRegister extends DeferredRegisterWrapper<MenuType<?>> {

  public MenuTypeDeferredRegister(String modID) {
    super(Registry.MENU, modID);
  }

  /**
   * Registers a container type
   * @param name     Container name
   * @param factory  Container factory
   * @param <C>      Container type
   * @return  Registry object containing the container type
   */
  public <C extends AbstractContainerMenu> RegistryObject<MenuType<C>> register(String name, ExtendedScreenHandlerType.ExtendedFactory<C> factory) {
    return register.register(name, () -> new ExtendedScreenHandlerType(factory));
  }
}
