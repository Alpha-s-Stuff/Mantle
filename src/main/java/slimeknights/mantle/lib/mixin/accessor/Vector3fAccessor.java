package slimeknights.mantle.lib.mixin.accessor;

import com.mojang.math.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Vector3f.class)
public interface Vector3fAccessor {
	@Accessor("x")
	void setX(float x);

	@Accessor("y")
	void setY(float y);

	@Accessor("z")
	void setZ(float z);
}
