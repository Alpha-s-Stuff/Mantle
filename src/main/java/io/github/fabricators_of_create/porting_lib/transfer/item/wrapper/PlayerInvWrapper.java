package io.github.fabricators_of_create.porting_lib.transfer.item.wrapper;

import io.github.fabricators_of_create.porting_lib.transfer.item.CombinedInvWrapper;
import net.minecraft.world.entity.player.Inventory;

public class PlayerInvWrapper extends CombinedInvWrapper {
	public PlayerInvWrapper(Inventory inv) {
		super(new PlayerMainInvWrapper(inv), new PlayerArmorInvWrapper(inv), new PlayerOffhandInvWrapper(inv));
	}
}
