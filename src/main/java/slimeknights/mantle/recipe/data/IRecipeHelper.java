package slimeknights.mantle.recipe.data;

import net.fabricmc.fabric.api.resource.conditions.v1.ConditionJsonProvider;
import net.fabricmc.fabric.api.resource.conditions.v1.DefaultResourceConditions;
import net.minecraft.core.Registry;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Interface for common resource location and condition methods
 */
@SuppressWarnings("unused")
public interface IRecipeHelper {
  /* Location helpers */

  /** Gets the ID of the mod adding recipes */
  String getModId();

  /**
   * Gets a resource location for the mod
   * @param name  Location path
   * @return  Location for the mod
   */
  default ResourceLocation modResource(String name) {
    return new ResourceLocation(getModId(), name);
  }

  /**
   * Gets a resource location string for Tinkers
   * @param id  Location path
   * @return  Location for Tinkers
   */
  default String modPrefix(String id) {
    return getModId() + ":" + id;
  }

  /**
   * Prefixes the resource location path with the given value
   * @param loc     Name to use
   * @param prefix  Prefix value
   * @return  Resource location path
   */
  default ResourceLocation wrap(ResourceLocation loc, String prefix, String suffix) {
    return modResource(prefix + loc.getPath() + suffix);
  }
  /**
   * Prefixes the resource location path with the given value
   * @param location  Entry registry name to use
   * @param prefix    Prefix value
   * @return  Resource location path
   */
  default ResourceLocation prefix(ResourceLocation location, String prefix) {
    return modResource(prefix + location.getPath());
  }


  /* Tags and conditions */

  /**
   * Gets a tag by name
   * @param modId  Mod ID for tag
   * @param name   Tag name
   * @return  Tag instance
   */
  default TagKey<Item> getItemTag(String modId, String name) {
    return TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation(modId, name));
  }

  /**
   * Gets a tag by name
   * @param modId  Mod ID for tag
   * @param name   Tag name
   * @return  Tag instance
   */
  default TagKey<Fluid> getFluidTag(String modId, String name) {
    return TagKey.create(Registry.FLUID_REGISTRY, new ResourceLocation(modId, name));
  }

  /**
   * Creates a condition for a tag existing
   * @param name  Forge tag name
   * @return  Condition for tag existing
   */
  default ConditionJsonProvider tagCondition(String name) {
    return DefaultResourceConditions.itemTagsPopulated(TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation("c", name)));
  }

  /**
   * Creates a consumer instance with the added conditions
   * @param consumer    Base consumer
   * @param conditions  Extra conditions
   * @return  Wrapped consumer
   */
  default Consumer<FinishedRecipe> withCondition(Consumer<FinishedRecipe> consumer, ConditionJsonProvider... conditions) {
    ConsumerWrapperBuilder builder = ConsumerWrapperBuilder.wrap();
    for (ConditionJsonProvider condition : conditions) {
      builder.addCondition(condition);
    }
    return builder.build(consumer);
  }
}
