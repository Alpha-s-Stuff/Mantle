package slimeknights.mantle.fluid;

import io.github.fabricators_of_create.porting_lib.mixin.accessors.common.accessor.BucketItemAccessor;
import io.github.fabricators_of_create.porting_lib.transfer.TransferUtil;
import io.github.fabricators_of_create.porting_lib.util.FluidStack;
import io.github.fabricators_of_create.porting_lib.util.FluidTextUtil;
import io.github.fabricators_of_create.porting_lib.util.FluidUnit;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import slimeknights.mantle.Mantle;
import slimeknights.mantle.config.Config;
import slimeknights.mantle.fluid.transfer.FluidContainerTransferManager;
import slimeknights.mantle.fluid.transfer.IFluidContainerTransfer;
import slimeknights.mantle.fluid.transfer.IFluidContainerTransfer.TransferResult;
import slimeknights.mantle.transfer.item.ItemHandlerHelper;

/**
 * Alternative to {@link net.minecraftforge.fluids.FluidUtil} since no one has time to make the forge util not a buggy mess
 */
@SuppressWarnings("unused")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FluidTransferHelper {
  private static final String KEY_FILLED = Mantle.makeDescriptionId("block", "tank.filled");
  private static final String KEY_FILLED_DROPLET = Mantle.makeDescriptionId("block", "tank.filled.droplets");
  private static final String KEY_DRAINED = Mantle.makeDescriptionId("block", "tank.drained");
  private static final String KEY_DRAINED_DROPLET = Mantle.makeDescriptionId("block", "tank.drained.droplets");

  public static String getKeyFilled() {
    return Config.FLUID_UNIT.get() == FluidUnit.MILIBUCKETS ? KEY_FILLED : KEY_FILLED_DROPLET;
  }

  public static String getKeyDrained() {
    return Config.FLUID_UNIT.get() == FluidUnit.MILIBUCKETS ? KEY_DRAINED : KEY_DRAINED_DROPLET;
  }

  /**
   * Attempts to transfer fluid
   * @param input    Fluid source
   * @param output   Fluid destination
   * @param maxFill  Maximum to transfer
   * @return  True if transfer succeeded
   */
  public static FluidStack tryTransfer(Storage<FluidVariant> input, Storage<FluidVariant> output, long maxFill) {
    for (StorageView<FluidVariant> view : input) {
      if (view.isResourceBlank()) continue;
      FluidVariant resource = view.getResource();
      long maxExtracted;

      // check how much can be extracted
      try (Transaction extractionTestTransaction = Transaction.openOuter()) {
        maxExtracted = view.extract(resource, maxFill, extractionTestTransaction);
        extractionTestTransaction.abort();
      }

      try (Transaction transferTransaction = Transaction.openOuter()) {
        // check how much can be inserted
        long accepted = output.insert(resource, maxExtracted, transferTransaction);

        // extract it, or rollback if the amounts don't match
        long drained = view.extract(resource, accepted, transferTransaction);
        if (drained != accepted) {
          Mantle.logger.error("Lost {} fluid during transfer", drained - accepted);
        }
        transferTransaction.commit();
        return new FluidStack(view.getResource(), drained);
      }
    }
    return FluidStack.EMPTY;
  }

  /**
   * Attempts to interact with a flilled bucket on a fluid tank. This is unique as it handles fish buckets, which don't expose fluid capabilities
   * @param world    World instance
   * @param pos      Block position
   * @param player   Player
   * @param hand     Hand
   * @param hit      Hit side
   * @param offset   Direction to place fish
   * @return True if using a bucket
   */
  public static boolean interactWithBucket(Level world, BlockPos pos, Player player, InteractionHand hand, Direction hit, Direction offset) {
    ItemStack held = player.getItemInHand(hand);
    if (held.getItem() instanceof BucketItem bucket) {
      Fluid fluid = ((BucketItemAccessor)bucket).port_lib$getContent();
      if (fluid != Fluids.EMPTY) {
        if (!world.isClientSide) {
          Storage<FluidVariant> handler = FluidStorage.SIDED.find(world, pos, hit);
          if (handler != null) {
              FluidStack fluidStack = new FluidStack(((BucketItemAccessor)bucket).port_lib$getContent(), FluidConstants.BUCKET);
              // must empty the whole bucket
              if (handler.simulateInsert(fluidStack.getType(), fluidStack.getAmount(), null) == FluidConstants.BUCKET) {
                try (Transaction t = TransferUtil.getTransaction()) {
                  handler.insert(fluidStack.getType(), fluidStack.getAmount(), t);
                  t.commit();
                }
                bucket.checkExtraContent(player, world, held, pos.relative(offset));
                world.playSound(null, pos, FluidVariantAttributes.getEmptySound(FluidVariant.of(fluid)), SoundSource.BLOCKS, 1.0F, 1.0F);
                player.displayClientMessage(Component.translatable(getKeyFilled(), FluidTextUtil.getUnicodeMillibuckets(FluidConstants.BUCKET, Config.FLUID_UNIT.get(), true), fluidStack.getDisplayName()), true);
                if (!player.isCreative()) {
                  player.setItemInHand(hand, held.getRecipeRemainder());
                }
              }
          }
        }
        return true;
      }
    }
    return false;
  }

  /** Plays the sound from filling a TE */
  private static void playEmptySound(Level world, BlockPos pos, Player player, FluidStack transferred) {
    world.playSound(null, pos, FluidVariantAttributes.getHandlerOrDefault(transferred.getFluid()).getEmptySound(transferred.getType()).orElse(SoundEvents.BUCKET_EMPTY), SoundSource.BLOCKS, 1.0F, 1.0F);
    player.displayClientMessage(Component.translatable(getKeyFilled(), FluidTextUtil.getUnicodeMillibuckets(transferred.getAmount(), Config.FLUID_UNIT.get(), true), transferred.getDisplayName()), true);
  }

  /** Plays the sound from draining a TE */
  private static void playFillSound(Level world, BlockPos pos, Player player, FluidStack transferred) {
    world.playSound(null, pos, FluidVariantAttributes.getHandlerOrDefault(transferred.getFluid()).getFillSound(transferred.getType())
      .or(() -> transferred.getFluid().getPickupSound())
      .orElse(SoundEvents.BUCKET_FILL), SoundSource.BLOCKS, 1.0F, 1.0F);
    player.displayClientMessage(Component.translatable(getKeyDrained(), FluidTextUtil.getUnicodeMillibuckets(transferred.getAmount(), Config.FLUID_UNIT.get(), true), transferred.getDisplayName()), true);
  }

  /**
   * Base logic to interact with a tank
   * @param world   World instance
   * @param pos     Tank position
   * @param player  Player instance
   * @param hand    Hand used
   * @param hit     Hit position
   * @return  True if further interactions should be blocked, false otherwise
   */
  public static boolean interactWithFluidItem(Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
    // success if the item is a fluid handler, regardless of if fluid moved
    ItemStack stack = player.getItemInHand(hand);
    Direction face = hit.getDirection();
    // fetch capability before copying, bit more work when its a fluid handler, but saves copying time when its not
    if (!stack.isEmpty()) {
      // only server needs to transfer stuff
      // TE must have a capability
      Storage<FluidVariant> teHandler = FluidStorage.SIDED.find(world, pos, face);
      if (teHandler != null) {
        ItemStack copy = ItemHandlerHelper.copyStackWithSize(stack, 1);

        // if the item has a capability, do a direct transfer
        if (FluidStorage.ITEM.find(stack, ContainerItemContext.withConstant(stack)) != null) {
          if (!world.isClientSide) {
            Storage<FluidVariant> itemHandler = ContainerItemContext.forPlayerInteraction(player, hand).find(FluidStorage.ITEM);
            // first, try filling the TE from the item
            FluidStack transferred = tryTransfer(itemHandler, teHandler, Long.MAX_VALUE);
            if (!transferred.isEmpty()) {
              playEmptySound(world, pos, player, transferred);
            } else {
              // if that failed, try filling the item handler from the TE
              transferred = tryTransfer(teHandler, itemHandler, Integer.MAX_VALUE);
              if (!transferred.isEmpty()) {
                playFillSound(world, pos, player, transferred);
              }
            }
          }
          return true;
        }

        // fallback to JSON based transfer
        if (FluidContainerTransferManager.INSTANCE.mayHaveTransfer(stack)) {
          // only actually transfer on the serverside, client just has items
          if (!world.isClientSide) {
            FluidStack currentFluid = TransferUtil.simulateExtractAnyFluid(teHandler, Long.MAX_VALUE);
            IFluidContainerTransfer transfer = FluidContainerTransferManager.INSTANCE.getTransfer(stack, currentFluid);
            if (transfer != null) {
              TransferResult result = transfer.transfer(stack, currentFluid, teHandler);
              if (result != null) {
                if (result.didFill()) {
                  playFillSound(world, pos, player, result.fluid());
                } else {
                  playEmptySound(world, pos, player, result.fluid());
                }
                player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, result.stack()));
              }
            }
          }
          return true;
        }
      }
    }
    return false;
  }

  /**
   * Utility to try fluid item then bucket
   * @param world   World instance
   * @param pos     Tank position
   * @param player  Player instance
   * @param hand    Hand used
   * @param hit     Hit position
   * @return  True if interacted
   */
  public static boolean interactWithTank(Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
    return interactWithFluidItem(world, pos, player, hand, hit)
           || interactWithBucket(world, pos, player, hand, hit.getDirection(), hit.getDirection());
  }
}
