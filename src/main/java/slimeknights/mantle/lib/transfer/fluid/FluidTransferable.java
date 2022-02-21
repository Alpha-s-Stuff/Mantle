package slimeknights.mantle.lib.transfer.fluid;

import javax.annotation.Nullable;

import net.minecraft.core.Direction;
import slimeknights.mantle.lib.util.LazyOptional;

public interface FluidTransferable {
	@Nullable
  LazyOptional<IFluidHandler> getFluidHandler(@Nullable Direction direction);
}
