package slimeknights.mantle.registration;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.ObjectHolder;
import slimeknights.mantle.Mantle;
import slimeknights.mantle.block.entity.MantleHangingSignBlockEntity;
import slimeknights.mantle.block.entity.MantleSignBlockEntity;

import static slimeknights.mantle.registration.RegistrationHelper.injected;

/**
 * Various objects registered under Mantle
 */
public class MantleRegistrations {
  private MantleRegistrations() {}

  public static BlockEntityType<MantleSignBlockEntity> SIGN = injected();

  public static BlockEntityType<MantleHangingSignBlockEntity> HANGING_SIGN = injected();
}
