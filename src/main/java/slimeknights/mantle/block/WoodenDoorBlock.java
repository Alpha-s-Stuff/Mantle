package slimeknights.mantle.block;

import net.fabricmc.fabric.api.registry.LandPathNodeTypesRegistry;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.pathfinder.BlockPathTypes;

import javax.annotation.Nullable;

public class WoodenDoorBlock extends DoorBlock implements LandPathNodeTypesRegistry.StaticPathNodeTypeProvider {
  public WoodenDoorBlock(Properties builder, BlockSetType blockSetType) {
    super(builder, blockSetType);
    LandPathNodeTypesRegistry.register(this, this);
  }

  @Nullable
  @Override
  public BlockPathTypes getPathNodeType(BlockState state, boolean neighbor) {
    return state.getValue(OPEN) ? BlockPathTypes.DOOR_OPEN : BlockPathTypes.DOOR_WOOD_CLOSED;
  }
}
