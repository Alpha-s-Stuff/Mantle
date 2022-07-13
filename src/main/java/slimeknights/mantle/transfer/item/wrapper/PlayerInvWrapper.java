package slimeknights.mantle.transfer.item.wrapper;

import net.minecraft.world.entity.player.Inventory;
import slimeknights.mantle.transfer.item.CombinedInvWrapper;

public class PlayerInvWrapper extends CombinedInvWrapper {
	public PlayerInvWrapper(Inventory inv) {
		super(new PlayerMainInvWrapper(inv), new PlayerArmorInvWrapper(inv), new PlayerOffhandInvWrapper(inv));
	}
}
