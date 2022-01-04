package slimeknights.mantle.lib.extensions;

import com.mojang.math.Matrix3f;
import org.jetbrains.annotations.NotNull;

public interface Matrix3fExtensions {
	float[] create$writeMatrix();

	void create$set(@NotNull Matrix3f other);
}
