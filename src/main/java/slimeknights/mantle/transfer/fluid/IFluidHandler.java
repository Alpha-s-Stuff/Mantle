package slimeknights.mantle.transfer.fluid;

import io.github.fabricators_of_create.porting_lib.util.FluidStack;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import org.jetbrains.annotations.ApiStatus;

public interface IFluidHandler {
	int getTanks();
	FluidStack getFluidInTank(int tank);
	long getTankCapacity(int tank);
	long fill(FluidStack stack, boolean sim); // returns amount filled
	FluidStack drain(FluidStack stack, boolean sim); // returns amount drained
	FluidStack drain(long amount, boolean sim); // returns amount drained
	default boolean isFluidValid(int tank, FluidStack stack) { return true; }
}
