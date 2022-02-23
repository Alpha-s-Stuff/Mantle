package slimeknights.mantle.lib.mixin.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.RailState;

@Mixin(RailState.class)
public interface RailStateAccessor {
	@Accessor("pos")
	BlockPos mantle$getPos();

	@Invoker("canConnectTo")
	boolean mantle$canConnectTo(RailState railState);

	@Invoker("removeSoftConnections")
	void mantle$removeSoftConnections();
}
