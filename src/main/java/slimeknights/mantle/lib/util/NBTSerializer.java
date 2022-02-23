package slimeknights.mantle.lib.util;

import net.minecraft.nbt.CompoundTag;

public class NBTSerializer {
	public static void deserializeNBT(Object o, CompoundTag nbt) {
		((NBTSerializable) o).mantle$deserializeNBT(nbt);
	}

	public static CompoundTag serializeNBT(Object o) {
		return ((NBTSerializable) o).mantle$serializeNBT();
	}
}
