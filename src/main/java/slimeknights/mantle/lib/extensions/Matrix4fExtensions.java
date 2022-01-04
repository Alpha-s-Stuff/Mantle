package slimeknights.mantle.lib.extensions;

import com.mojang.math.Matrix4f;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public interface Matrix4fExtensions {
	void create$set(@NotNull Matrix4f other);

	@Contract(mutates = "this")
	void create$fromFloatArray(float[] floats);

	float[] create$writeMatrix();

	void create$setTranslation(float x, float y, float z);
}
