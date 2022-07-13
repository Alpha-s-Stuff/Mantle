package io.github.fabricators_of_create.porting_lib.transfer.item.wrapper;

import io.github.fabricators_of_create.porting_lib.transfer.item.InvWrapper;
import net.minecraft.world.entity.player.Inventory;

public class PlayerOffhandInvWrapper extends RangedWrapper {
	public PlayerOffhandInvWrapper(Inventory inv) {
		super(new InvWrapper(inv), inv.items.size() + inv.armor.size(),
				inv.items.size() + inv.armor.size() + inv.offhand.size());
	}
}
