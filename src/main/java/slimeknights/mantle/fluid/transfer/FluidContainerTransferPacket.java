package slimeknights.mantle.fluid.transfer;

import com.google.common.collect.ImmutableSet;
import lombok.RequiredArgsConstructor;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.Item;
import slimeknights.mantle.network.packet.IThreadsafePacket;

import java.util.Set;

/** Packet to sync fluid container transfer */
@RequiredArgsConstructor
public class FluidContainerTransferPacket implements IThreadsafePacket {
  private final Set<Item> items;

  public FluidContainerTransferPacket(FriendlyByteBuf buffer) {
    ImmutableSet.Builder<Item> builder = ImmutableSet.builder();
    int size = buffer.readVarInt();
    for (int i = 0; i < size; i++) {
      builder.add(BuiltInRegistries.ITEM.get(buffer.readResourceLocation()));
    }
    this.items = builder.build();
  }

  @Override
  public void encode(FriendlyByteBuf buffer) {
    buffer.writeVarInt(items.size());
    for (Item item : items) {
      buffer.writeResourceLocation(BuiltInRegistries.ITEM.getKey(item));
    }
  }

  @Override
  public void handleThreadsafe(Context context) {
    FluidContainerTransferManager.INSTANCE.setContainerItems(items);
  }
}
