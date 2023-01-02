package slimeknights.mantle.client.model.data;

import com.google.common.base.Preconditions;
import io.github.fabricators_of_create.porting_lib.model.data.ModelData;
import io.github.fabricators_of_create.porting_lib.model.data.ModelProperty;
import lombok.RequiredArgsConstructor;

import javax.annotation.Nullable;

/**
 * IModelData instance that holds a single model data property. Will be more efficient than the map implementation in cases without more properties.
 * If you need more than one model property, use {@link net.minecraftforge.client.model.data.ModelDataMap} instead.
 * @param <D>  Property type, for data validation
 */
@RequiredArgsConstructor
public class SinglePropertyData<D> {
  private final ModelProperty<D> property;
  private D data = null;

  /**
   * Creates an instance with a property and preset data
   * @param property  Property for this instance
   * @param data      Original data
   */
  public SinglePropertyData(ModelProperty<D> property, @Nullable D data) {
    Preconditions.checkArgument(property.test(data), "Value is invalid for this property");
    this.property = property;
    this.data = data;
  }

  public boolean hasProperty(ModelProperty<?> prop) {
    return prop == this.property;
  }

  @SuppressWarnings("unchecked")
  @Nullable
  public <T> T getData(ModelProperty<T> prop) {
    if (prop == this.property) {
      return (T) data;
    }
    return null;
  }

  @SuppressWarnings("unchecked")
  @Nullable
  public <T> T setData(ModelProperty<T> prop, T data) {
    Preconditions.checkArgument(prop.test(data), "Value is invalid for this property");
    if (prop == this.property) {
      this.data = (D) data;
      return data;
    }
    return null;
  }
}
