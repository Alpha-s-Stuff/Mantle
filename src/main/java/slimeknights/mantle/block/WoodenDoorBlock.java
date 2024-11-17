package slimeknights.mantle.block;

import net.fabricmc.fabric.api.registry.LandPathNodeTypesRegistry;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.pathfinder.PathType;

import javax.annotation.Nullable;

public class WoodenDoorBlock extends DoorBlock implements LandPathNodeTypesRegistry.StaticPathNodeTypeProvider {
  public WoodenDoorBlock(BlockSetType type, Properties builder) {
    super(type, builder);
    LandPathNodeTypesRegistry.register(this, this);
  }

  @Nullable
  @Override
  public PathType getPathNodeType(BlockState state, boolean neighbor) {
    return state.getValue(OPEN) ? PathType.DOOR_OPEN : PathType.DOOR_WOOD_CLOSED;
  }
}
