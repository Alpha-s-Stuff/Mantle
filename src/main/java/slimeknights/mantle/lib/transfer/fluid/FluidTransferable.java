package slimeknights.mantle.lib.transfer.fluid;

import net.minecraft.core.Direction;
import slimeknights.mantle.lib.util.LazyOptional;

import javax.annotation.Nullable;

public interface FluidTransferable {
	@Nullable
  LazyOptional<IFluidHandler> getFluidHandler(@Nullable Direction direction);
}
