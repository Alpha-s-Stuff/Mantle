package slimeknights.mantle.lib.extensions;

import com.mojang.math.Matrix4f;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public interface Matrix4fExtensions {
	void mantle$set(@NotNull Matrix4f other);

	@Contract(mutates = "this")
	void mantle$fromFloatArray(float[] floats);

	default float[] mantle$writeMatrix() {
    return new float[0];
  };

	default void mantle$setTranslation(float x, float y, float z) {}

  default void mantle$multiplyBackward(Matrix4f other) {}
}
