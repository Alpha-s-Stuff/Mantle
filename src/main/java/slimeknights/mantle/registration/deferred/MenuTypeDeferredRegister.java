package slimeknights.mantle.registration.deferred;

import io.github.fabricators_of_create.porting_lib.util.DeferredHolder;
import io.github.fabricators_of_create.porting_lib.util.DeferredRegister;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

/**
 * Deferred register for menu types, automatically mapping a factory argument in {@link ExtendedScreenHandlerType.ExtendedFactory}
 */
@SuppressWarnings("unused")
public class MenuTypeDeferredRegister extends DeferredRegisterWrapper<MenuType<?>, DeferredRegister<MenuType<?>>> {

  public MenuTypeDeferredRegister(String modID) {
    super(DeferredRegister.create(Registries.MENU, modID));
  }

  /**
   * Registers a container type
   * @param name     Container name
   * @param factory  Container factory
   * @param <C>      Container type
   * @return  Registry object containing the container type
   */
  public <C extends AbstractContainerMenu, D> DeferredHolder<MenuType<?>, MenuType<C>> register(String name, ExtendedScreenHandlerType.ExtendedFactory<C, D> factory, StreamCodec<? extends RegistryFriendlyByteBuf, D> codec) {
    return register.register(name, () -> new ExtendedScreenHandlerType(factory, codec));
  }
}
