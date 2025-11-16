package slimeknights.mantle.item;

import io.github.fabricators_of_create.porting_lib.fluids.FluidStack;
import lombok.Getter;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;

/** Represents a capability handler for a container with a constant fluid */
public class ConstantFluidContainerWrapper extends SnapshotParticipant<Boolean> implements SingleSlotStorage<FluidVariant> {

  /** Contained fluid */
  private final FluidStack fluid;
  /** If true, the container is now empty */
  private boolean empty = false;
  /** Item stack representing the current state */
  @Getter
  @Nonnull
  protected final ContainerItemContext container;
  /** Empty version of the container */
  private final ItemStack emptyStack;

  public ConstantFluidContainerWrapper(FluidStack fluid, ContainerItemContext container, ItemStack emptyStack) {
    this.fluid = fluid;
    this.container = container;
    this.emptyStack = emptyStack;
  }

  public ConstantFluidContainerWrapper(FluidStack fluid, ContainerItemContext container) {
    this(fluid, container, container.getItemVariant().toStack().getRecipeRemainder());
  }

  @Override
  public long getCapacity() {
    return fluid.getAmount();
  }

  @Override
  public boolean isResourceBlank() {
    return empty || fluid.getType().isBlank();
  }

  @Override
  public FluidVariant getResource() {
    return fluid.getType();
  }

  @Nonnull
  @Override
  public long getAmount() {
    return fluid.getAmount();
  }

  @Override
  public boolean supportsInsertion() {
    return false;
  }

  @Override
  public long insert(FluidVariant resource, long maxAmount, TransactionContext tx) {
    return 0;
  }

  @Override
  public long extract(FluidVariant resource, long maxAmount, TransactionContext tx) {
    StoragePreconditions.notBlankNotNegative(resource, maxAmount);

    // cannot drain if: already drained, requested the wrong type, or requested too little
    if (empty || resource.getFluid() != fluid.getFluid() || maxAmount < fluid.getAmount()) {
      return 0;
    }
    updateSnapshots(tx);
    if (container.exchange(ItemVariant.of(emptyStack), emptyStack.getCount(), tx) == emptyStack.getCount()) {
      empty = true;
      return fluid.getAmount();
    }
    return 0;
  }

  @Override
  protected Boolean createSnapshot() {
    return empty;
  }

  @Override
  protected void readSnapshot(Boolean snapshot) {
    empty = snapshot;
  }
}
