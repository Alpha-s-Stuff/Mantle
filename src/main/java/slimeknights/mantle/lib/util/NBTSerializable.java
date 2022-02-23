package slimeknights.mantle.lib.util;

import net.minecraft.nbt.CompoundTag;

public interface NBTSerializable {
	CompoundTag mantle$serializeNBT();

	void mantle$deserializeNBT(CompoundTag nbt);
}
