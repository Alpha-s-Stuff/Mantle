package slimeknights.mantle.lib.mixin.common;

import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import slimeknights.mantle.lib.extensions.SlotExtensions;
import slimeknights.mantle.lib.util.MixinHelper;

@Mixin(Slot.class)
public class SlotMixin implements SlotExtensions {

  @Shadow
  @Final
  private int slot;

  @Unique
  @Override
  public int getSlotIndex() {
    return slot;
  }

  @Unique
  private Pair<ResourceLocation, ResourceLocation> backgroundPair;

  @Unique
  @Override
  public Slot setBackground(ResourceLocation atlas, ResourceLocation sprite) {
    this.backgroundPair = Pair.of(atlas, sprite);
    return MixinHelper.cast(this);
  }

  /**
   * @author AlphaMode
   * The base method just returns null
   * Yes I did just @Overwrite this :ioa:
   */
  @Nullable
  @Overwrite
  public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
    return backgroundPair;
  }
}
