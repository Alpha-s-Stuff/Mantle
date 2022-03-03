package slimeknights.mantle.lib.extensions;

import slimeknights.mantle.lib.model.BlockModelConfiguration;

public interface BlockModelExtensions {

  default BlockModelConfiguration getGeometry() {
	  throw new RuntimeException("I think your kinda bad, this shouldn't happen!");
  }
}
