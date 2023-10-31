package slimeknights.mantle;

import io.github.fabricators_of_create.porting_lib.config.ConfigRegistry;
import io.github.fabricators_of_create.porting_lib.config.ConfigType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredientSerializer;
import net.minecraft.Util;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import slimeknights.mantle.block.entity.MantleSignBlockEntity;
import slimeknights.mantle.command.MantleCommand;
import slimeknights.mantle.config.Config;
import slimeknights.mantle.data.predicate.block.BlockPredicate;
import slimeknights.mantle.data.predicate.block.SetBlockPredicate;
import slimeknights.mantle.data.predicate.block.TagBlockPredicate;
import slimeknights.mantle.data.predicate.entity.LivingEntityPredicate;
import slimeknights.mantle.data.predicate.entity.MobTypePredicate;
import slimeknights.mantle.data.predicate.entity.TagEntityPredicate;
import slimeknights.mantle.datagen.MantleTags;
import slimeknights.mantle.fluid.transfer.EmptyFluidContainerTransfer;
import slimeknights.mantle.fluid.transfer.EmptyFluidWithNBTTransfer;
import slimeknights.mantle.fluid.transfer.FillFluidContainerTransfer;
import slimeknights.mantle.fluid.transfer.FillFluidWithNBTTransfer;
import slimeknights.mantle.fluid.transfer.FluidContainerTransferManager;
import slimeknights.mantle.item.LecternBookItem;
import slimeknights.mantle.loot.MantleLoot;
import slimeknights.mantle.network.MantleNetwork;
import slimeknights.mantle.recipe.MantleRecipeSerializers;
import slimeknights.mantle.recipe.crafting.ShapedFallbackRecipe;
import slimeknights.mantle.recipe.crafting.ShapedRetexturedRecipe;
import slimeknights.mantle.recipe.helper.TagPreference;
import slimeknights.mantle.recipe.ingredient.FluidContainerIngredient;
import slimeknights.mantle.registration.MantleRegistrations;
import slimeknights.mantle.registration.adapter.BlockEntityTypeRegistryAdapter;
import slimeknights.mantle.registration.adapter.RegistryAdapter;

/**
 * Mantle
 *
 * Central mod object for Mantle
 *
 * @author Sunstrike <sun@sunstrike.io>
 */
public class Mantle implements ModInitializer {
  public static final String modId = "mantle";
  public static final Logger logger = LogManager.getLogger("Mantle");

  /* Instance of this mod, used for grabbing prototype fields */
  public static Mantle instance;

  /* Proxies for sides, used for graphics processing */
  @Override
  public void onInitialize() {
    ConfigRegistry.registerConfig(modId, ConfigType.CLIENT, Config.CLIENT_SPEC);
    ConfigRegistry.registerConfig(modId, ConfigType.SERVER, Config.SERVER_SPEC);

    FluidContainerTransferManager.INSTANCE.init();
    MantleTags.init();

    instance = this;
    commonSetup();
    this.registerCapabilities();
    this.registerRecipeSerializers();
    this.registerBlockEntities();
    MantleLoot.registerGlobalLootModifiers();
    UseBlockCallback.EVENT.register(LecternBookItem::interactWithBlock);
  }

  private void registerCapabilities() {
//    OffhandCooldownTracker.register();
  }

  private void commonSetup() {
    MantleNetwork.INSTANCE.network.initServerListener();
    MantleNetwork.registerPackets();
    MantleCommand.init();
//    OffhandCooldownTracker.register();
    TagPreference.init();
  }

  private void registerRecipeSerializers() {
    RegistryAdapter<RecipeSerializer<?>> adapter = new RegistryAdapter<>(BuiltInRegistries.RECIPE_SERIALIZER, Mantle.modId);
    MantleRecipeSerializers.CRAFTING_SHAPED_FALLBACK = adapter.register(new ShapedFallbackRecipe.Serializer(), "crafting_shaped_fallback");
    MantleRecipeSerializers.CRAFTING_SHAPED_RETEXTURED = adapter.register(new ShapedRetexturedRecipe.Serializer(), "crafting_shaped_retextured");

//    CraftingHelper.register(TagEmptyCondition.SERIALIZER);
    CustomIngredientSerializer.register(FluidContainerIngredient.SERIALIZER);

    // fluid container transfer
    FluidContainerTransferManager.TRANSFER_LOADERS.registerDeserializer(EmptyFluidContainerTransfer.ID, EmptyFluidContainerTransfer.DESERIALIZER);
    FluidContainerTransferManager.TRANSFER_LOADERS.registerDeserializer(FillFluidContainerTransfer.ID, FillFluidContainerTransfer.DESERIALIZER);
    FluidContainerTransferManager.TRANSFER_LOADERS.registerDeserializer(EmptyFluidWithNBTTransfer.ID, EmptyFluidWithNBTTransfer.DESERIALIZER);
    FluidContainerTransferManager.TRANSFER_LOADERS.registerDeserializer(FillFluidWithNBTTransfer.ID, FillFluidWithNBTTransfer.DESERIALIZER);

    // predicates
    {
      // block predicates
      BlockPredicate.LOADER.register(getResource("and"), BlockPredicate.AND);
      BlockPredicate.LOADER.register(getResource("or"), BlockPredicate.OR);
      BlockPredicate.LOADER.register(getResource("inverted"), BlockPredicate.INVERTED);
      BlockPredicate.LOADER.register(getResource("requires_tool"), BlockPredicate.REQUIRES_TOOL.getLoader());
      BlockPredicate.LOADER.register(getResource("set"), SetBlockPredicate.LOADER);
      BlockPredicate.LOADER.register(getResource("tag"), TagBlockPredicate.LOADER);
      // entity predicates
      LivingEntityPredicate.LOADER.register(getResource("and"), LivingEntityPredicate.AND);
      LivingEntityPredicate.LOADER.register(getResource("or"), LivingEntityPredicate.OR);
      LivingEntityPredicate.LOADER.register(getResource("inverted"), LivingEntityPredicate.INVERTED);
      LivingEntityPredicate.LOADER.register(getResource("any"), LivingEntityPredicate.ANY.getLoader());
      LivingEntityPredicate.LOADER.register(getResource("fire_immune"), LivingEntityPredicate.FIRE_IMMUNE.getLoader());
      LivingEntityPredicate.LOADER.register(getResource("water_sensitive"), LivingEntityPredicate.WATER_SENSITIVE.getLoader());
      LivingEntityPredicate.LOADER.register(getResource("on_fire"), LivingEntityPredicate.ON_FIRE.getLoader());
      LivingEntityPredicate.LOADER.register(getResource("tag"), TagEntityPredicate.LOADER);
      LivingEntityPredicate.LOADER.register(getResource("mob_type"), MobTypePredicate.LOADER);
      // register mob types
      MobTypePredicate.MOB_TYPES.register(new ResourceLocation("undefined"), MobType.UNDEFINED);
      MobTypePredicate.MOB_TYPES.register(new ResourceLocation("undead"), MobType.UNDEAD);
      MobTypePredicate.MOB_TYPES.register(new ResourceLocation("arthropod"), MobType.ARTHROPOD);
      MobTypePredicate.MOB_TYPES.register(new ResourceLocation("illager"), MobType.ILLAGER);
      MobTypePredicate.MOB_TYPES.register(new ResourceLocation("water"), MobType.WATER);
    }
  }

  private void registerBlockEntities() {
    BlockEntityTypeRegistryAdapter adapter = new BlockEntityTypeRegistryAdapter();
    MantleRegistrations.SIGN = adapter.register(MantleSignBlockEntity::new, "sign", MantleSignBlockEntity::buildSignBlocks);
  }

  /**
   * Gets a resource location for Mantle
   * @param name  Name
   * @return  Resource location instance
   */
  public static ResourceLocation getResource(String name) {
    return new ResourceLocation(modId, name);
  }

  /**
   * Makes a translation key for the given name
   * @param base  Base name, such as "block" or "gui"
   * @param name  Object name
   * @return  Translation key
   */
  public static String makeDescriptionId(String base, String name) {
    return Util.makeDescriptionId(base, getResource(name));
  }

  /**
   * Makes a translation text component for the given name
   * @param base  Base name, such as "block" or "gui"
   * @param name  Object name
   * @return  Translation key
   */
  public static MutableComponent makeComponent(String base, String name) {
    return Component.translatable(makeDescriptionId(base, name));
  }
}
