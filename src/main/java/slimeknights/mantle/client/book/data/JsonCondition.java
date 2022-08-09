package slimeknights.mantle.client.book.data;

import com.google.gson.JsonObject;
import lombok.Getter;
import net.fabricmc.fabric.api.resource.conditions.v1.ConditionJsonProvider;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditions;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;

public class JsonCondition {
  @Getter
  private final ConditionJsonProvider conditionJsonProvider;
  @Getter
  private final ResourceLocation conditionId;
  @Getter
  private final JsonObject object;

  public JsonCondition(@Nullable ConditionJsonProvider conditionJsonProvider) {
    this.conditionJsonProvider = conditionJsonProvider;
    if (conditionJsonProvider != null)
      this.conditionId = conditionJsonProvider.getConditionId();
    else
      this.conditionId = null;
    this.object = null;
  }

  public JsonCondition(ResourceLocation id) {
    this(id, null);
  }

  public JsonCondition(ResourceLocation id, JsonObject object) {
    this.conditionJsonProvider = null;
    this.conditionId = id;
    this.object = object;
  }

  public JsonCondition() {
    this.conditionJsonProvider = null;
    this.conditionId = null;
    this.object = null;
  }

  public boolean test() {
    if (conditionId == null || object == null)
      return false;
    return ResourceConditions.get(conditionId).test(object);
  }
}
