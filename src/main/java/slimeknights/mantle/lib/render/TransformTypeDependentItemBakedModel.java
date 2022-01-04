package slimeknights.mantle.lib.render;

import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.resources.model.BakedModel;

public interface TransformTypeDependentItemBakedModel {
	BakedModel create$handlePerspective(TransformType cameraTransformType);
}
