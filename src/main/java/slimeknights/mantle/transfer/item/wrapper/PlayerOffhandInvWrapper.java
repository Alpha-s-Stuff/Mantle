package slimeknights.mantle.transfer.item.wrapper;

import slimeknights.mantle.transfer.item.InvWrapper;
import slimeknights.mantle.transfer.item.wrapper.RangedWrapper;
import net.minecraft.world.entity.player.Inventory;

public class PlayerOffhandInvWrapper extends RangedWrapper {
	public PlayerOffhandInvWrapper(Inventory inv) {
		super(new InvWrapper(inv), inv.items.size() + inv.armor.size(),
				inv.items.size() + inv.armor.size() + inv.offhand.size());
	}
}
