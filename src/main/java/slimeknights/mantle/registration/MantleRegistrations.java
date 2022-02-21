package slimeknights.mantle.registration;

import net.minecraft.world.level.block.entity.BlockEntityType;
import slimeknights.mantle.Mantle;
import slimeknights.mantle.block.entity.MantleSignBlockEntity;

import static slimeknights.mantle.registration.RegistrationHelper.injected;

/**
 * Various objects registered under Mantle
 */
public class MantleRegistrations {
  private MantleRegistrations() {}

  public static final BlockEntityType<MantleSignBlockEntity> SIGN = injected();
}
