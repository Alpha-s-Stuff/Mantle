package slimeknights.mantle.lib.mixin.common;

import com.mojang.math.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Accessor;
import slimeknights.mantle.lib.extensions.Vector3fExtensions;

@Mixin(Vector3f.class)
public class Vector3fMixin implements Vector3fExtensions {

  @Shadow
  public float x, y, z;

  @Unique
  @Override
  public void setX(float x) {
    this.x = x;
  }

  @Unique
  @Override
  public void setY(float y) {
    this.y = y;
  }

  @Unique
  @Override
  public void setZ(float z) {
    this.z = z;
  }
}
