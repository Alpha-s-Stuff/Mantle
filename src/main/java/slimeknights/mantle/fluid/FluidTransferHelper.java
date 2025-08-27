package slimeknights.mantle.fluid;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.SoundAction;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.templates.EmptyFluidHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import slimeknights.mantle.Mantle;
import slimeknights.mantle.fluid.transfer.FluidContainerTransferManager;
import slimeknights.mantle.fluid.transfer.IFluidContainerTransfer;
import slimeknights.mantle.fluid.transfer.IFluidContainerTransfer.TransferDirection;
import slimeknights.mantle.fluid.transfer.IFluidContainerTransfer.TransferResult;

/**
 * Alternative to {@link net.minecraftforge.fluids.FluidUtil} since no one has time to make the forge util not a buggy mess
 */
@SuppressWarnings("unused")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FluidTransferHelper {
  private static final String KEY_FILLED = Mantle.makeDescriptionId("block", "tank.filled");
  private static final String KEY_DRAINED = Mantle.makeDescriptionId("block", "tank.drained");

  /** Gets the given sound from the fluid */
  public static SoundEvent getSound(FluidStack fluid, SoundAction action, SoundEvent fallback) {
    SoundEvent event = fluid.getFluid().getFluidType().getSound(fluid, action);
    if (event == null) {
      return fallback;
    }
    return event;
  }

  /** Gets the empty sound for a fluid */
  public static SoundEvent getEmptySound(FluidStack fluid) {
    return getSound(fluid, SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY);
  }

  /** Gets the fill sound for a fluid */
  public static SoundEvent getFillSound(FluidStack fluid) {
    return getSound(fluid, SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL);
  }

  /**
   * Attempts to transfer fluid
   * @param input    Fluid source
   * @param output   Fluid destination
   * @param maxFill  Maximum to transfer
   * @return  True if transfer succeeded
   */
  public static FluidStack tryTransfer(IFluidHandler input, IFluidHandler output, int maxFill) {
    return tryTransfer(input, output, input.drain(maxFill, FluidAction.SIMULATE));
  }

  /**
   * Attempts to transfer fluid
   * @param input    Fluid source
   * @param output   Fluid destination
   * @param fluid    Fluid to transfer, will not be modified. Precondition is it must be valid to drain from the input.
   * @return  True if transfer succeeded
   */
  public static FluidStack tryTransfer(IFluidHandler input, IFluidHandler output, FluidStack fluid) {
    if (!fluid.isEmpty()) {
      // next, find out how much we can fill
      int simulatedFill = output.fill(fluid.copy(), FluidAction.SIMULATE);
      if (simulatedFill > 0) {
        // actually drain, use the fluid we successfully filled with just in case that changes
        FluidStack drainedFluid = input.drain(new FluidStack(fluid, simulatedFill), FluidAction.EXECUTE);
        if (!drainedFluid.isEmpty()) {
          // acutally fill
          int actualFill = output.fill(drainedFluid.copy(), FluidAction.EXECUTE);
          if (actualFill != drainedFluid.getAmount()) {
            Mantle.logger.error("Lost {} fluid during transfer", drainedFluid.getAmount() - actualFill);
          }
        }
        return drainedFluid;
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
      Fluid fluid = bucket.getFluid();
      if (fluid != Fluids.EMPTY) {
        if (!world.isClientSide) {
          BlockEntity te = world.getBlockEntity(pos);
          if (te != null) {
            te.getCapability(ForgeCapabilities.FLUID_HANDLER, hit)
              .ifPresent(handler -> {
                FluidStack fluidStack = new FluidStack(bucket.getFluid(), FluidType.BUCKET_VOLUME);
                // must empty the whole bucket
                if (handler.fill(fluidStack, FluidAction.SIMULATE) == FluidType.BUCKET_VOLUME) {
                  SoundEvent sound = getEmptySound(fluidStack);
                  handler.fill(fluidStack, FluidAction.EXECUTE);
                  bucket.checkExtraContent(player, world, held, pos.relative(offset));
                  world.playSound(null, pos, sound, SoundSource.BLOCKS, 1.0F, 1.0F);
                  player.displayClientMessage(Component.translatable(KEY_FILLED, FluidType.BUCKET_VOLUME, fluidStack.getDisplayName()), true);
                  if (!player.isCreative()) {
                    player.setItemInHand(hand, held.getCraftingRemainingItem());
                  }
                }
              });
          }
        }
        return true;
      }
    }
    return false;
  }

  /** Plays the sound from filling a TE */
  public static void playEmptySound(Level world, BlockPos pos, Player player, FluidStack transferred) {
    world.playSound(null, pos, getEmptySound(transferred), SoundSource.BLOCKS, 1.0F, 1.0F);
    player.displayClientMessage(Component.translatable(KEY_FILLED, transferred.getAmount(), transferred.getDisplayName()), true);
  }

  /** Plays the sound from draining a TE */
  public static void playFillSound(Level world, BlockPos pos, Player player, FluidStack transferred) {
    world.playSound(null, pos, getFillSound(transferred), SoundSource.BLOCKS, 1.0F, 1.0F);
    player.displayClientMessage(Component.translatable(KEY_DRAINED, transferred.getAmount(), transferred.getDisplayName()), true);
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
      BlockEntity te = world.getBlockEntity(pos);
      if (te != null) {
        // TE must have a capability
        LazyOptional<IFluidHandler> teCapability = te.getCapability(ForgeCapabilities.FLUID_HANDLER, face);
        if (teCapability.isPresent()) {
          IFluidHandler teHandler = teCapability.orElse(EmptyFluidHandler.INSTANCE);

          // fallback to JSON based transfer
          if (FluidContainerTransferManager.INSTANCE.mayHaveTransfer(stack)) {
            // only actually transfer on the serverside, client just has items
            if (!world.isClientSide) {
              FluidStack currentFluid = teHandler.drain(Integer.MAX_VALUE, FluidAction.SIMULATE);
              IFluidContainerTransfer transfer = FluidContainerTransferManager.INSTANCE.getTransfer(stack, currentFluid);
              if (transfer != null) {
                TransferResult result = transfer.transfer(stack, currentFluid, teHandler, TransferDirection.AUTO);
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

          // if the item has a capability, do a direct transfer
          ItemStack copy = ItemHandlerHelper.copyStackWithSize(stack, 1);
          LazyOptional<IFluidHandlerItem> itemCapability = copy.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM);
          if (itemCapability.isPresent()) {
            if (!world.isClientSide) {
              IFluidHandlerItem itemHandler = itemCapability.resolve().orElseThrow();
              // first, try filling the TE from the item
              FluidStack transferred = tryTransfer(itemHandler, teHandler, Integer.MAX_VALUE);
              if (!transferred.isEmpty()) {
                playEmptySound(world, pos, player, transferred);
              } else {
                // if that failed, try filling the item handler from the TE
                transferred = tryTransfer(teHandler, itemHandler, Integer.MAX_VALUE);
                if (!transferred.isEmpty()) {
                  playFillSound(world, pos, player, transferred);
                }
              }
              // if either worked, update the player's inventory
              if (!transferred.isEmpty()) {
                player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, itemHandler.getContainer()));
              }
            }
            return true;
          }
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

  /**
   * Attempts to transfer fluid from the passed stack into a tank.
   * @param teHandler  Tank handler
   * @param stack      Input stack, may be modified
   * @param direction  Determines whether we may empty the item, fill, or both
   * @return  Resulting stack after transfer
   */
  public static ItemStack interactWithTankSlot(IFluidHandler teHandler, ItemStack stack, TransferDirection direction) {
    if (!stack.isEmpty()) {
      // fallback to JSON based transfer
      if (FluidContainerTransferManager.INSTANCE.mayHaveTransfer(stack)) {
        // only actually transfer on the serverside, client just has items
        FluidStack currentFluid = teHandler.drain(Integer.MAX_VALUE, FluidAction.SIMULATE);
        IFluidContainerTransfer transfer = FluidContainerTransferManager.INSTANCE.getTransfer(stack, currentFluid);
        if (transfer != null) {
          TransferResult result = transfer.transfer(stack, currentFluid, teHandler, direction);
          if (result != null) {
            stack.shrink(1);
            return result.stack();
          }
        }
      }

      // if the item has a capability, do a direct transfer
      ItemStack copy = ItemHandlerHelper.copyStackWithSize(stack, 1);
      LazyOptional<IFluidHandlerItem> itemCapability = copy.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM);
      if (itemCapability.isPresent()) {
        IFluidHandlerItem itemHandler = itemCapability.resolve().orElseThrow();
        // first, try filling the TE from the item
        FluidStack transferred = FluidStack.EMPTY;
        // reverse means try TE to item first
        if (direction == TransferDirection.REVERSE) {
          transferred = tryTransfer(teHandler, itemHandler, Integer.MAX_VALUE);
        }
        // if not reverse or reverse failed, try filling TE from item
        if (direction.canEmpty() && transferred.isEmpty()) {
          transferred = tryTransfer(itemHandler, teHandler, Integer.MAX_VALUE);
        }
        // if that failed, try filling the item handler from the TE
        if (direction != TransferDirection.REVERSE && direction.canFill() && transferred.isEmpty()) {
          transferred = tryTransfer(teHandler, itemHandler, Integer.MAX_VALUE);
        }
        // if either worked, update the player's inventory
        if (!transferred.isEmpty()) {
          stack.shrink(1);
          return itemHandler.getContainer();
        }
      }
    }
    return ItemStack.EMPTY;
  }

  /**
   * Attempts to transfer fluid into the passed stack from the given handler.
   * Similar to {@link #interactWithTankSlot(IFluidHandler, ItemStack, TransferDirection)} except filtered and unable to set direction.
   * @param teHandler  Tank handler
   * @param stack      Input stack, may be modified
   * @param fluid      Determines the fluid used to fill the item
   * @return  Resulting stack after transfer
   */
  public static ItemStack fillFromTankSlot(IFluidHandler teHandler, ItemStack stack, FluidStack fluid) {
    if (!stack.isEmpty()) {
      // fallback to JSON based transfer
      if (FluidContainerTransferManager.INSTANCE.mayHaveTransfer(stack)) {
        // only actually transfer on the serverside, client just has items
        IFluidContainerTransfer transfer = FluidContainerTransferManager.INSTANCE.getTransfer(stack, fluid);
        if (transfer != null) {
          TransferResult result = transfer.transfer(stack, fluid, teHandler, TransferDirection.FILL_ITEM);
          if (result != null) {
            stack.shrink(1);
            return result.stack();
          }
        }
      }

      // if the item has a capability, do a direct transfer
      ItemStack copy = ItemHandlerHelper.copyStackWithSize(stack, 1);
      LazyOptional<IFluidHandlerItem> itemCapability = copy.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM);
      if (itemCapability.isPresent()) {
        IFluidHandlerItem itemHandler = itemCapability.resolve().orElseThrow();
        // first, try filling the TE from the item
        FluidStack transferred = tryTransfer(teHandler, itemHandler, fluid.copy());
        if (!transferred.isEmpty()) {
          stack.shrink(1);
          return itemHandler.getContainer();
        }
      }
    }
    return ItemStack.EMPTY;
  }
  
  /**
   * Same as {@link net.minecraft.world.item.ItemUtils#createFilledResult(ItemStack, Player, ItemStack)} but doesn't shrink results or check creative.
   * Useful in UIs along {@link #interactWithTankSlot(IFluidHandler, ItemStack, TransferDirection)} or {@link #fillFromTankSlot(IFluidHandler, ItemStack, FluidStack)}
   */
  public static ItemStack getOrTransferFilled(Player player, ItemStack emptyStack, ItemStack filledStack) {
    // if no more helpd
    if (emptyStack.isEmpty()) {
      return filledStack;
    }
    if (!player.getInventory().add(filledStack)) {
      player.drop(filledStack, false);
    }
    return emptyStack;
  }
}
