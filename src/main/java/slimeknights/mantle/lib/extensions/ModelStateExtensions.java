package slimeknights.mantle.lib.extensions;

import com.mojang.math.Transformation;

public interface ModelStateExtensions {
  default Transformation getPartTransformation(Object part) {
    return Transformation.identity();
  }
}
