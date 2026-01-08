package slimeknights.mantle.client.model;

import com.google.common.base.Preconditions;
import io.github.fabricators_of_create.porting_lib.models.CustomParticleIconModel;
import net.fabricmc.fabric.api.blockview.v2.RenderDataBlockEntity;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

/**
 * A container for data to be passed to {@link BakedModel} instances.
 * <p>
 * All objects stored in here <b>MUST BE IMMUTABLE OR THREAD-SAFE</b>.
 * Properties will be accessed from another thread.
 *
 * @see ModelProperty
 * @see RenderDataBlockEntity#getRenderData()
 * @see FabricBakedModel#emitBlockQuads(BlockAndTintGetter, BlockState, BlockPos, Supplier, RenderContext)
 * @see CustomParticleIconModel#getParticleIcon(Object)
 */
public final class ModelData
{
  public static final ModelData EMPTY = ModelData.builder().build();

  private final Map<ModelProperty<?>, Object> properties;

  private ModelData(Map<ModelProperty<?>, Object> properties)
  {
    this.properties = properties;
  }

  public Set<ModelProperty<?>> getProperties()
  {
    return properties.keySet();
  }

  public boolean has(ModelProperty<?> property)
  {
    return properties.containsKey(property);
  }

  @Nullable
  public <T> T get(ModelProperty<T> property)
  {
    return (T) properties.get(property);
  }

  public Builder derive()
  {
    return new Builder(this);
  }

  public static Builder builder()
  {
    return new Builder(null);
  }

  public static final class Builder
  {
    private final Map<ModelProperty<?>, Object> properties = new IdentityHashMap<>();

    private Builder(@Nullable ModelData parent)
    {
      if (parent != null)
      {
        properties.putAll(parent.properties);
      }
    }

    @Contract("_, _ -> this")
    public <T> Builder with(ModelProperty<T> property, T value)
    {
      Preconditions.checkState(property.test(value), "The provided value is invalid for this property.");
      properties.put(property, value);
      return this;
    }

    @Contract("-> new")
    public ModelData build()
    {
      return new ModelData(Collections.unmodifiableMap(properties));
    }
  }
}
