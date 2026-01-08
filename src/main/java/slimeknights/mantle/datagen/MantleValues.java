package slimeknights.mantle.datagen;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;

/** Contains some constants used for values shared across SlimeKnights mods */
public interface MantleValues {
  /** Amount of mb of a bowl, such as mushroom stew or beetroot soup */
  long BOWL = 20250;
  /** Amount of mb of a bottle, such as a potion or honey bottle */
  long BOTTLE = FluidConstants.BOTTLE;
  /** Division of water */
  long DROP = BOTTLE / 5;
  /** Division of an edible bowl or bottle */
  long SIP = BOWL / 5;
}
