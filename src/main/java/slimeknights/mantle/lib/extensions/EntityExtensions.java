package slimeknights.mantle.lib.extensions;

import java.util.Collection;
import java.util.Collections;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.item.ItemEntity;

public interface EntityExtensions {
	default CompoundTag getExtraCustomData() {
    return null;
  }

	default Collection<ItemEntity> captureDrops() {
    return Collections.emptyList();
  }

	default Collection<ItemEntity> captureDrops(Collection<ItemEntity> value) {
    return Collections.emptyList();
  }
}
