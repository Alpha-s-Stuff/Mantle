package slimeknights.mantle.lib.extensions;

public interface TierExtension {

  @javax.annotation.Nullable default net.minecraft.tags.Tag<net.minecraft.world.level.block.Block> getTag() { return null; }
}
