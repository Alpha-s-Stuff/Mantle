package slimeknights.mantle.lib.extensions;

import com.mojang.math.Matrix3f;
import org.jetbrains.annotations.NotNull;

public interface Matrix3fExtensions {
	default float[] mantle$writeMatrix() {
    return new float[2];
  }

	default void mantle$set(@NotNull Matrix3f other) {}
}
