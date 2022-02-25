package slimeknights.mantle.lib.mixin.common;

import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Transformation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import slimeknights.mantle.lib.extensions.TransformationExtensions;

@Mixin(Transformation.class)
public class TransformationMixin implements TransformationExtensions {

  @Shadow
  @Final
  private Matrix4f matrix;

  @Unique
  private Matrix3f normalTransform = null;

  @Unique
  @Override
  public Matrix3f getNormalMatrix() {
    checkNormalTransform();
    return normalTransform;
  }

  @Unique
  private void checkNormalTransform() {
    if (normalTransform == null) {
      normalTransform = new Matrix3f(matrix);
      normalTransform.invert();
      normalTransform.transpose();
    }
  }
}
