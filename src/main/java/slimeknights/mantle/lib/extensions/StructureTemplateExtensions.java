package slimeknights.mantle.lib.extensions;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.Vec3;

public interface StructureTemplateExtensions {
	List<StructureTemplate.StructureEntityInfo> mantle$getEntities();

	Vec3 mantle$transformedVec3d(StructurePlaceSettings placementIn, Vec3 pos);

	List<StructureTemplate.StructureEntityInfo> mantle$processEntityInfos(@Nullable StructureTemplate template, LevelAccessor world, BlockPos blockPos, StructurePlaceSettings settings, List<StructureTemplate.StructureEntityInfo> infos);

	void mantle$addEntitiesToWorld(ServerLevelAccessor world, BlockPos blockPos, StructurePlaceSettings settings);
}
