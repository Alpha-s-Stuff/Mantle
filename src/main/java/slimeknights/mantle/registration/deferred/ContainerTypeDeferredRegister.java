package slimeknights.mantle.registration.deferred;

import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.fabricmc.fabric.impl.screenhandler.ExtendedScreenHandlerType;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
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
  public <C extends AbstractContainerMenu> RegistryObject<MenuType<C>> register(String name, ScreenHandlerRegistry.ExtendedClientHandlerFactory<C> factory) {
    return register.register(name, () -> new ExtendedScreenHandlerType(factory));
  }
}
