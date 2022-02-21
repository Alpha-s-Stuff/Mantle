package slimeknights.mantle.network.packet;

import java.util.function.Supplier;

/**
 * Packet instance that automatically wraps the logic in {@link ISimplePacket.Context#enqueueWork(Runnable)} for thread safety
 */
public interface IThreadsafePacket extends ISimplePacket {
  @Override
  default void handle(Supplier<ISimplePacket.Context> supplier) {
    ISimplePacket.Context context = supplier.get();
    context.enqueueWork(() -> handleThreadsafe(context));
    context.setPacketHandled(true);
  }

  /**
   * Handles receiving the packet on the correct thread
   * Packet is automatically set to handled as well by the base logic
   * @param context  Packet context
   */
  void handleThreadsafe(ISimplePacket.Context context);
}
