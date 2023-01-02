package slimeknights.mantle.registration;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.item.Item;
import slimeknights.mantle.fabric.fluid.SimpleDirectionalFluid;
import slimeknights.mantle.util.SimpleFlowableFluid;

import java.util.function.Supplier;

/**
 * Fluid properties builder class, since the Forge one requires too many suppliers that we do not have access to yet
 */
@Accessors(fluent = true)
@Setter
@RequiredArgsConstructor
public class FluidBuilder {
  private final FluidAttributeBuilder attributes;
  private boolean canMultiply = false;
  private Supplier<? extends Item> bucket;
  private Supplier<? extends LiquidBlock> block;
  private int slopeFindDistance = 4;
  private int levelDecreasePerBlock = 1;
  private float explosionResistance = 1;
  private int tickRate = 5;

  /** Sets {@code canMultiply} to true */
  public FluidBuilder canMultiply() {
    canMultiply = true;
    return this;
  }

  /**
   * Builds Forge fluid properties from this builder
   * @param still    Still fluid supplier
   * @param flowing  Flowing supplier
   * @return  Forge fluid properties
   */
  public SimpleFlowableFluid.Properties build(Supplier<? extends Fluid> still, Supplier<? extends Fluid> flowing) {
    SimpleFlowableFluid.Properties properties = new SimpleFlowableFluid.Properties(still, flowing, this.attributes)
        .flowSpeed(this.slopeFindDistance)
        .levelDecreasePerBlock(this.levelDecreasePerBlock)
        .blastResistance(this.explosionResistance)
        .tickRate(this.tickRate)
        .block(this.block)
        .bucket(this.bucket);
    if (this.canMultiply) {
      properties.canMultiply();
    }
    return properties;
  }

  /**
   * Builds Directional fluid properties from this builder
   * @param still    Still fluid supplier
   * @param flowing  Flowing supplier
   * @return  Forge fluid properties
   */
  public SimpleDirectionalFluid.Properties buildUpsideDownFluid(Supplier<? extends Fluid> still, Supplier<? extends Fluid> flowing) {
    SimpleDirectionalFluid.Properties properties = new SimpleDirectionalFluid.Properties(still, flowing, this.attributes)
      .flowSpeed(this.slopeFindDistance)
      .levelDecreasePerBlock(this.levelDecreasePerBlock)
      .blastResistance(this.explosionResistance)
      .tickRate(this.tickRate)
      .block(this.block)
      .flowDirection(Direction.UP)
      .bucket(this.bucket);
    if (this.canMultiply) {
      properties.canMultiply();
    }
    return properties;
  }

}
