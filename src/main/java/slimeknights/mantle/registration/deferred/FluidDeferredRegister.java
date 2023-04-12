package slimeknights.mantle.registration.deferred;

import io.github.fabricators_of_create.porting_lib.util.EnvExecutor;
import io.github.fabricators_of_create.porting_lib.util.RegistryObject;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Material;
import slimeknights.mantle.fabric.fluid.SimpleDirectionalFluid;
import slimeknights.mantle.fluid.attributes.FluidAttributes;
import slimeknights.mantle.registration.DelayedSupplier;
import slimeknights.mantle.registration.FluidAttributeClientHandler;
import slimeknights.mantle.registration.FluidAttributeHandler;
import slimeknights.mantle.registration.FluidBuilder;
import slimeknights.mantle.registration.ItemProperties;
import slimeknights.mantle.registration.object.FluidObject;
import slimeknights.mantle.util.SimpleFlowableFluid;
import slimeknights.mantle.util.SimpleFlowableFluid.Properties;

import java.util.function.Function;
import java.util.function.Supplier;

@SuppressWarnings({"unused", "WeakerAccess"})
public class FluidDeferredRegister extends DeferredRegisterWrapper<Fluid> {
  private final SynchronizedDeferredRegister<Block> blockRegister;
  private final SynchronizedDeferredRegister<Item> itemRegister;
  public FluidDeferredRegister(String modID) {
    super(Registries.FLUID, modID);
    this.blockRegister = SynchronizedDeferredRegister.create(Registries.BLOCK, modID);
    this.itemRegister = SynchronizedDeferredRegister.create(Registries.ITEM, modID);
  }

  @Override
  public void register() {
    blockRegister.register();
    itemRegister.register();
  }

  /**
   * Registers a fluid to the registry
   * @param name  Name of the fluid to register
   * @param sup   Fluid supplier
   * @param <I>   Fluid type
   * @return  Fluid to supply
   */
  public <I extends Fluid> RegistryObject<I> registerFluid(final String name, final Supplier<? extends I> sup) {
    return register.register(name, sup);
  }

  /**
   * Registers a fluid with still, flowing, block, and bucket
   * @param name     Fluid name
   * @param tagName  Name for tagging under forge
   * @param builder  Properties builder
   * @param still    Function to create still from the properties
   * @param flowing  Function to create flowing from the properties
   * @param block    Function to create block from the fluid supplier
   * @param <F>      Fluid type
   * @return  Fluid object
   */
  public <F extends SimpleFlowableFluid> FluidObject<F> register(String name, String tagName, FluidBuilder builder, Function<Properties,? extends F> still,
                                                                 Function<Properties,? extends F> flowing, Function<Supplier<? extends FlowingFluid>,? extends LiquidBlock> block) {

    // have to create still and flowing later, as the props need these suppliers
    DelayedSupplier<F> stillDelayed = new DelayedSupplier<>();
    DelayedSupplier<F> flowingDelayed = new DelayedSupplier<>();

    // create block and bucket, they just need a still supplier
    RegistryObject<LiquidBlock> blockObj = blockRegister.register(name + "_fluid", () -> block.apply(stillDelayed));
    builder.bucket(itemRegister.register(name + "_bucket", () -> new BucketItem(stillDelayed.get(), ItemProperties.BUCKET_PROPS)));

    // create props with the suppliers
    Properties props = builder.block(blockObj).build(stillDelayed, flowingDelayed);

    // create fluids now that we have props
    Supplier<F> stillSup = registerFluid(name, () -> {
      F fluid = Registry.register(BuiltInRegistries.FLUID, new ResourceLocation(this.modID, name), still.apply(props));
      FluidAttributes attributes = props.attributes.build(fluid);
      FluidVariantAttributes.register(fluid, new FluidAttributeHandler(attributes));
      EnvExecutor.runWhenOn(EnvType.CLIENT, () -> () -> FluidRenderHandlerRegistry.INSTANCE.register(fluid, new FluidAttributeClientHandler(attributes)));
      return fluid;
    });
    stillDelayed.setSupplier(stillSup);
    Supplier<F> flowingSup = registerFluid("flowing_" + name, () -> {
      F fluid = Registry.register(BuiltInRegistries.FLUID, new ResourceLocation(this.modID, "flowing_" + name), flowing.apply(props));
      FluidAttributes attributes = props.attributes.build(fluid);
      FluidVariantAttributes.register(fluid, new FluidAttributeHandler(attributes));
      EnvExecutor.runWhenOn(EnvType.CLIENT, () -> () -> FluidRenderHandlerRegistry.INSTANCE.register(fluid, new FluidAttributeClientHandler(attributes)));
      return fluid;
    });
    flowingDelayed.setSupplier(flowingSup);

    // return the final nice object
    return new FluidObject<>(resource(name), tagName, stillSup, flowingSup, blockObj);
  }

  /**
   * Registers a fluid with still, flowing, block, bucket, and a common forgen name
   * @param name     Fluid name
   * @param builder  Properties builder
   * @param still    Function to create still from the properties
   * @param flowing  Function to create flowing from the properties
   * @param block    Function to create block from the fluid supplier
   * @param <F>      Fluid type
   * @return  Fluid object
   */
  public <F extends SimpleFlowableFluid> FluidObject<F> register(String name, FluidBuilder builder, Function<Properties,? extends F> still,
      Function<Properties,? extends F> flowing, Function<Supplier<? extends FlowingFluid>,? extends LiquidBlock> block) {
    return register(name, name, builder, still, flowing, block);
  }

  /**
   * Registers a fluid with still, flowing, block, and bucket using the default fluid block
   * @param name       Fluid name
   * @param tagName    Name for tagging under forge
   * @param builder    Properties builder
   * @param still      Function to create still from the properties
   * @param flowing    Function to create flowing from the properties
   * @param material   Block material
   * @param lightLevel Block light level
   * @param <F>      Fluid type
   * @return  Fluid object
   */
  public <F extends SimpleFlowableFluid> FluidObject<F> register(String name, String tagName, FluidAttributes.Builder builder,
                                                                 Function<Properties,? extends F> still, Function<Properties,? extends F> flowing, Material material, int lightLevel) {
    return register(
      name, tagName, new FluidBuilder(builder.luminosity(lightLevel)).explosionResistance(100f), still, flowing,
      fluid -> new LiquidBlock(fluid.get(), FabricBlockSettings.of(material).noCollission().strength(100.0F).noLootTable().lightLevel(state -> lightLevel))
    );
  }

  /**
   * Registers a fluid with still, flowing, block, and bucket
   * @param name     Fluid name
   * @param tagName  Name for tagging under forge
   * @param builder  Properties builder
   * @param still    Function to create still from the properties
   * @param flowing  Function to create flowing from the properties
   * @param block    Function to create block from the fluid supplier
   * @param <F>      Fluid type
   * @return  Fluid object
   */
  public <F extends SimpleDirectionalFluid> FluidObject<F> registerUpsideDown(String name, String tagName, FluidBuilder builder, Function<SimpleDirectionalFluid.Properties,? extends F> still,
                                                                              Function<SimpleDirectionalFluid.Properties,? extends F> flowing, Function<Supplier<? extends FlowingFluid>,? extends LiquidBlock> block) {

    // have to create still and flowing later, as the props need these suppliers
    DelayedSupplier<F> stillDelayed = new DelayedSupplier<>();
    DelayedSupplier<F> flowingDelayed = new DelayedSupplier<>();

    // create block and bucket, they just need a still supplier
    RegistryObject<LiquidBlock> blockObj = blockRegister.register(name + "_fluid", () -> block.apply(stillDelayed));
    builder.bucket(itemRegister.register(name + "_bucket", () -> new BucketItem(stillDelayed.get(), ItemProperties.BUCKET_PROPS)));

    // create props with the suppliers
    SimpleDirectionalFluid.Properties props = builder.block(blockObj).buildUpsideDownFluid(stillDelayed, flowingDelayed);

    // create fluids now that we have props
    Supplier<F> stillSup = registerFluid(name, () -> {
      F fluid = Registry.register(BuiltInRegistries.FLUID, new ResourceLocation(this.modID, name), still.apply(props));
      FluidAttributes attributes = props.attributes.build(fluid);
      FluidVariantAttributes.register(fluid, new FluidAttributeHandler(attributes));
      EnvExecutor.runWhenOn(EnvType.CLIENT, () -> () -> FluidRenderHandlerRegistry.INSTANCE.register(fluid, new FluidAttributeClientHandler(attributes)));
      return fluid;
    });
    stillDelayed.setSupplier(stillSup);
    Supplier<F> flowingSup = registerFluid("flowing_" + name, () -> {
      F fluid = Registry.register(BuiltInRegistries.FLUID, new ResourceLocation(this.modID, "flowing_" + name), flowing.apply(props));
      FluidAttributes attributes = props.attributes.build(fluid);
      FluidVariantAttributes.register(fluid, new FluidAttributeHandler(attributes));
      EnvExecutor.runWhenOn(EnvType.CLIENT, () -> () -> FluidRenderHandlerRegistry.INSTANCE.register(fluid, new FluidAttributeClientHandler(attributes)));
      return fluid;
    });
    flowingDelayed.setSupplier(flowingSup);

    // return the final nice object
    return new FluidObject<>(resource(name), tagName, stillSup, flowingSup, blockObj);
  }

  /**
   * Registers a fluid with still, flowing, block, and bucket using the default fluid block
   * @param name       Fluid name
   * @param builder    Properties builder
   * @param still      Function to create still from the properties
   * @param flowing    Function to create flowing from the properties
   * @param material   Block material
   * @param lightLevel Block light level
   * @param <F>      Fluid type
   * @return  Fluid object
   */
  public <F extends SimpleDirectionalFluid> FluidObject<F> registerUpsideDown(String name, FluidAttributes.Builder builder,
                                                                              Function<SimpleDirectionalFluid.Properties,? extends F> still, Function<SimpleDirectionalFluid.Properties,? extends F> flowing, Material material, int lightLevel) {
    return registerUpsideDown(name, name, builder, still, flowing, material, lightLevel);
  }

  /**
   * Registers a fluid with still, flowing, block, and bucket using the default fluid block
   * @param name       Fluid name
   * @param tagName    Name for tagging under forge
   * @param builder    Properties builder
   * @param still      Function to create still from the properties
   * @param flowing    Function to create flowing from the properties
   * @param material   Block material
   * @param lightLevel Block light level
   * @param <F>      Fluid type
   * @return  Fluid object
   */
  public <F extends SimpleDirectionalFluid> FluidObject<F> registerUpsideDown(String name, String tagName, FluidAttributes.Builder builder,
                                                                              Function<SimpleDirectionalFluid.Properties,? extends F> still, Function<SimpleDirectionalFluid.Properties,? extends F> flowing, Material material, int lightLevel) {
    return registerUpsideDown(
      name, tagName, new FluidBuilder(builder.luminosity(lightLevel)).explosionResistance(100f), still, flowing,
      fluid -> new LiquidBlock(fluid.get(), Block.Properties.of(material).noCollission().strength(100.0F).noLootTable().lightLevel(state -> lightLevel))
    );
  }

  /**
   * Registers a fluid with generic still, flowing, block, and bucket using the default Forge fluid
   * @param name       Fluid name
   * @param tagName    Name for tagging under forge
   * @param builder    Properties builder
   * @param material   Block material
   * @param lightLevel Block light level
   * @return  Fluid object
   */
  public FluidObject<SimpleFlowableFluid> register(String name, String tagName, FluidAttributes.Builder builder, Material material, int lightLevel) {
    return register(name, tagName, builder, SimpleFlowableFluid.Still::new, SimpleFlowableFluid.Flowing::new, material, lightLevel);
  }

  /**
   * Registers a fluid with generic still, flowing, block, and bucket using the default Forge fluid
   * @param name       Fluid name
   * @param builder    Properties builder
   * @param material   Block material
   * @param lightLevel Block light level
   * @return  Fluid object
   */
  public FluidObject<SimpleFlowableFluid> register(String name, FluidAttributes.Builder builder, Material material, int lightLevel) {
    return register(name, name, builder, material, lightLevel);
  }

  /**
   * Registers a fluid with still, flowing, block, and bucket using the default fluid block
   * @param name       Fluid name
   * @param builder    Properties builder
   * @param still      Function to create still from the properties
   * @param flowing    Function to create flowing from the properties
   * @param material   Block material
   * @param lightLevel Block light level
   * @param <F>      Fluid type
   * @return  Fluid object
   */
  public <F extends SimpleFlowableFluid> FluidObject<F> register(String name, FluidAttributes.Builder builder,
                                                                 Function<Properties,? extends F> still, Function<Properties,? extends F> flowing, Material material, int lightLevel) {
    return register(name, name, builder, still, flowing, material, lightLevel);
  }
}
