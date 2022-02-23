package slimeknights.mantle.lib.mixin.common;

import slimeknights.mantle.lib.extensions.StructureProcessorExtensions;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

@Mixin(StructureProcessor.class)
public abstract class StructureProcessorMixin implements StructureProcessorExtensions {
	@Override
	public StructureTemplate.StructureEntityInfo mantle$processEntity(LevelReader world, BlockPos seedPos, StructureTemplate.StructureEntityInfo rawEntityInfo,
																	  StructureTemplate.StructureEntityInfo entityInfo, StructurePlaceSettings placementSettings,
																	  StructureTemplate template) {
		return entityInfo;
	}
}
