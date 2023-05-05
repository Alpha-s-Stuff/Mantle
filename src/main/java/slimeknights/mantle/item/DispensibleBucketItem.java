package slimeknights.mantle.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.DispensibleContainerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.material.Fluid;

public class DispensibleBucketItem extends BucketItem {

  private static final DispenseItemBehavior DISPENSE_ITEM_BEHAVIOR = new DefaultDispenseItemBehavior() {
    private final DefaultDispenseItemBehavior defaultDispenseItemBehavior = new DefaultDispenseItemBehavior();

    @Override
    public ItemStack execute(BlockSource source, ItemStack stack) {
      DispensibleContainerItem container = (DispensibleContainerItem)stack.getItem();
      BlockPos blockpos = source.getPos().relative(source.getBlockState().getValue(DispenserBlock.FACING));
      Level level = source.getLevel();
      if (container.emptyContents(null, level, blockpos, null)) {
        container.checkExtraContent(null, level, stack, blockpos);
        return new ItemStack(Items.BUCKET);
      } else {
        return this.defaultDispenseItemBehavior.dispense(source, stack);
      }
    }
  };

  public DispensibleBucketItem(Fluid fluid, Properties properties) {
    super(fluid, properties);
    DispenserBlock.registerBehavior(this, DISPENSE_ITEM_BEHAVIOR);
  }
}
