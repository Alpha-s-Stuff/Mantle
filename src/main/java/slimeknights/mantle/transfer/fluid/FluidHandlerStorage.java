package slimeknights.mantle.transfer.fluid;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import slimeknights.mantle.transfer.TransferUtil;
import io.github.fabricators_of_create.porting_lib.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;

/**
 * Wraps an IFluidHandler in a Storage, for use outside Create
 */
@SuppressWarnings({"UnstableApiUsage"})
public class FluidHandlerStorage implements Storage<FluidVariant> {
	protected final IFluidHandler handler;

	public FluidHandlerStorage(IFluidHandler handler) {
		if (handler == null) {
			this.handler = EmptyTank.INSTANCE;
		} else {
			this.handler = handler;
		}
	}

	public IFluidHandler getHandler() {
		return handler;
	}

	@Override
	public long insert(FluidVariant resource, long maxAmount, TransactionContext transaction) {
		long remainder = handler.fill(new FluidStack(resource, maxAmount), true);

    TransferUtil.addEndCallback(transaction, (result) -> {
			if (result.wasCommitted()) {
				handler.fill(new FluidStack(resource, maxAmount), false);
			}
		});
		return remainder;
	}

	@Override
	public long extract(FluidVariant resource, long maxAmount, TransactionContext transaction) {
		FluidStack extracted = handler.drain(new FluidStack(resource, maxAmount), true);
		TransferUtil.addEndCallback(transaction, (result) -> {
			if (result.wasCommitted()) {
				handler.drain(new FluidStack(resource, maxAmount), false);
			}
		});
		return extracted.getAmount();
	}

	@Override
	public Iterator<StorageView<FluidVariant>> iterator() {
		int tanks = handler.getTanks();
		List<StorageView<FluidVariant>> views = new ArrayList<>();
		for (int i = 0; i < tanks; i++) {
			views.add(new TankStorageView(i, handler));
		}
		return views.iterator();
	}

	@Override
	@Nullable
	public StorageView<FluidVariant> exactView(FluidVariant resource) {
		for (StorageView<FluidVariant> view : this) {
			if (view.getResource().equals(resource)) {
				return view;
			}
		}
		return null;
	}

	public static class TankStorageView implements StorageView<FluidVariant> {
		protected final int tankIndex;
		protected final IFluidHandler owner;

		public TankStorageView(int tankIndex, IFluidHandler owner) {
			this.tankIndex = tankIndex;
			this.owner = owner;
		}

		@Override
		public long extract(FluidVariant resource, long maxAmount, TransactionContext transaction) {
			FluidStack drained = owner.drain(new FluidStack(resource, maxAmount), true);
			transaction.addCloseCallback((t, result) -> {
				if (result.wasCommitted()) {
					owner.drain(new FluidStack(resource, maxAmount), false);
				}
			});
			return drained.getAmount();
		}

		@Override
		public boolean isResourceBlank() {
			return owner.getFluidInTank(tankIndex).isEmpty();
		}

		@Override
		public FluidVariant getResource() {
			return owner.getFluidInTank(tankIndex).getType();
		}

		@Override
		public long getAmount() {
			return owner.getFluidInTank(tankIndex).getAmount();
		}

		@Override
		public long getCapacity() {
			return owner.getTankCapacity(tankIndex);
		}
	}
}
