package slimeknights.mantle.recipe.data;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Builds a recipe consumer wrapper, which adds some extra properties to wrap the result of another recipe
 */
@SuppressWarnings("UnusedReturnValue")
public class ConsumerWrapperBuilder {
  private final List<ICondition> conditions = new ArrayList<>();
  @Nullable
  private final IRecipeSerializer<?> override;
  @Nullable
  private final ResourceLocation overrideName;

  private ConsumerWrapperBuilder(@Nullable IRecipeSerializer<?> override, @Nullable ResourceLocation overrideName) {
    this.override = override;
    this.overrideName = overrideName;
  }

  /**
   * Creates a wrapper builder with the default serializer
   * @return Default serializer builder
   */
  public static ConsumerWrapperBuilder wrap() {
    return new ConsumerWrapperBuilder(null, null);
  }

  /**
   * Creates a wrapper builder with a serializer override
   * @param override Serializer override
   * @return Default serializer builder
   */
  public static ConsumerWrapperBuilder wrap(IRecipeSerializer<?> override) {
    return new ConsumerWrapperBuilder(override, null);
  }

  /**
   * Creates a wrapper builder with a serializer name override
   * @param override Serializer override
   * @return Default serializer builder
   */
  public static ConsumerWrapperBuilder wrap(ResourceLocation override) {
    return new ConsumerWrapperBuilder(null, override);
  }

  /**
   * Adds a conditional to the consumer
   * @param condition Condition to add
   * @return Added condition
   */
  public ConsumerWrapperBuilder addCondition(ICondition condition) {
    conditions.add(condition);
    return this;
  }

  /**
   * Builds the consumer for the wrapper builder
   * @param consumer Base consumer
   * @return Built wrapper consumer
   */
  public Consumer<IFinishedRecipe> build(Consumer<IFinishedRecipe> consumer) {
    return (recipe) -> consumer.accept(new Wrapped(recipe, conditions, override, overrideName));
  }

  private static class Wrapped implements IFinishedRecipe {
    private final IFinishedRecipe original;
    private final List<ICondition> conditions;
    @Nullable
    private final IRecipeSerializer<?> override;
    @Nullable
    private final ResourceLocation overrideName;

    private Wrapped(IFinishedRecipe original, List<ICondition> conditions, @Nullable IRecipeSerializer<?> override, @Nullable ResourceLocation overrideName) {
      // if wrapping another wrapper result, merge the two together
      if (original instanceof Wrapped) {
        Wrapped toMerge = (Wrapped) original;
        this.original = toMerge.original;
        this.conditions = ImmutableList.<ICondition>builder().addAll(toMerge.conditions).addAll(conditions).build();
        // consumer wrappers are processed inside out, so the innermost wrapped recipe is the one with the most recent serializer override
        if (toMerge.override != null || toMerge.overrideName != null) {
          this.override = toMerge.override;
          this.overrideName = toMerge.overrideName;
        } else {
          this.override = override;
          this.overrideName = overrideName;
        }
      } else {
        this.original = original;
        this.conditions = conditions;
        this.override = override;
        this.overrideName = overrideName;
      }
    }

    @Override
    public JsonObject serializeRecipe() {
      JsonObject json = new JsonObject();
      if (overrideName != null) {
        json.addProperty("type", overrideName.toString());
      } else {
        json.addProperty("type", Objects.requireNonNull(getType().getRegistryName()).toString());
      }
      this.serializeRecipeData(json);
      return json;
    }

    @Override
    public void serializeRecipeData(JsonObject json) {
      // add conditions on top
      if (!conditions.isEmpty()) {
        JsonArray conditionsArray = new JsonArray();
        for (ICondition condition : conditions) {
          conditionsArray.add(CraftingHelper.serialize(condition));
        }
        json.add("conditions", conditionsArray);
      }
      // serialize the normal recipe
      original.serializeRecipeData(json);
    }

    @Override
    public ResourceLocation getId() {
      return original.getId();
    }

    @Override
    public IRecipeSerializer<?> getType() {
      if (override != null) {
        return override;
      }
      return original.getType();
    }

    @Nullable
    @Override
    public JsonObject serializeAdvancement() {
      return original.serializeAdvancement();
    }

    @Nullable
    @Override
    public ResourceLocation getAdvancementId() {
      return original.getAdvancementId();
    }
  }
}
