package slimeknights.mantle.lib.extensions;

import java.util.Collection;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.item.ItemEntity;

public interface EntityExtensions {
	CompoundTag mantle$getExtraCustomData();

	Collection<ItemEntity> mantle$captureDrops();

	Collection<ItemEntity> mantle$captureDrops(Collection<ItemEntity> value);
}
