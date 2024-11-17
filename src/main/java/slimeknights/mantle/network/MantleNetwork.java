package slimeknights.mantle.network;

import io.github.fabricators_of_create.porting_lib.util.NetworkDirection;
import slimeknights.mantle.Mantle;
import slimeknights.mantle.fluid.transfer.FluidContainerTransferPacket;
import slimeknights.mantle.network.packet.ClearBookCachePacket;
import slimeknights.mantle.network.packet.DropLecternBookPacket;
import slimeknights.mantle.network.packet.OpenLecternBookPacket;
import slimeknights.mantle.network.packet.OpenNamedBookPacket;
import slimeknights.mantle.network.packet.SwingArmPacket;
import slimeknights.mantle.network.packet.UpdateHeldPagePacket;
import slimeknights.mantle.network.packet.UpdateLecternPagePacket;

public class MantleNetwork {
  /**
   * Registers packets into this network
   */
  public static void registerPackets() {
    NetworkWrapper.registerPacket(OpenLecternBookPacket.TYPE, OpenLecternBookPacket.CODEC, NetworkDirection.PLAY_TO_CLIENT);
    NetworkWrapper.registerPacket(UpdateHeldPagePacket.TYPE, UpdateHeldPagePacket.CODEC, NetworkDirection.PLAY_TO_SERVER);
    NetworkWrapper.registerPacket(UpdateLecternPagePacket.TYPE, UpdateLecternPagePacket.CODEC, NetworkDirection.PLAY_TO_SERVER);
    NetworkWrapper.registerPacket(DropLecternBookPacket.TYPE, DropLecternBookPacket::new, NetworkDirection.PLAY_TO_SERVER);
    NetworkWrapper.registerPacket(SwingArmPacket.TYPE, SwingArmPacket.CODEC, NetworkDirection.PLAY_TO_CLIENT);
    NetworkWrapper.registerPacket(OpenNamedBookPacket.TYPE, OpenNamedBookPacket.CODEC, NetworkDirection.PLAY_TO_CLIENT);
    NetworkWrapper.registerPacket(FluidContainerTransferPacket.TYPE, FluidContainerTransferPacket::new, NetworkDirection.PLAY_TO_CLIENT);
    NetworkWrapper.registerPacket(ClearBookCachePacket.TYPE, ClearBookCachePacket::new, NetworkDirection.PLAY_TO_CLIENT);
  }
}
