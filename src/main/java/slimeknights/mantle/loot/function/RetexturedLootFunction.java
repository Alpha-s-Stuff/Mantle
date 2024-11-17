package slimeknights.mantle.loot.function;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import slimeknights.mantle.Mantle;
import slimeknights.mantle.block.entity.IRetexturedBlockEntity;
import slimeknights.mantle.item.RetexturedBlockItem;
import slimeknights.mantle.loot.MantleLoot;

import java.util.List;
import java.util.Set;

/**
 * Applies the data for a retextured block to the dropped item. No configuration needed.
 */
@SuppressWarnings("WeakerAccess")
public class RetexturedLootFunction extends LootItemConditionalFunction {
  public static final MapCodec<RetexturedLootFunction> CODEC = RecordCodecBuilder.mapCodec(instance ->
    commonFields(instance).apply(instance, RetexturedLootFunction::new));

  /**
   * Creates a new instance from the given conditions
   * @param conditions Conditions list
   */
  public RetexturedLootFunction(List<LootItemCondition> conditions) {
    super(conditions);
  }

  /** Creates a new instance with no conditions */
  public RetexturedLootFunction() {
    super(List.of());
  }

  @Override
  public Set<LootContextParam<?>> getReferencedContextParams() {
    return ImmutableSet.of(LootContextParams.BLOCK_ENTITY);
  }

  @Override
  protected ItemStack run(ItemStack stack, LootContext context) {
    BlockEntity te = context.getParamOrNull(LootContextParams.BLOCK_ENTITY);
    if (te instanceof IRetexturedBlockEntity retextured) {
      RetexturedBlockItem.setTexture(stack, retextured.getTextureName());
    } else {
      String name = te == null ? "null" : te.getClass().getName();
      Mantle.logger.warn("Found wrong tile entity for loot function, expected IRetexturedTileEntity, found {}", name);
    }
    return stack;
  }

  @Override
  public LootItemFunctionType getType() {
    return MantleLoot.RETEXTURED_FUNCTION;
  }
}
