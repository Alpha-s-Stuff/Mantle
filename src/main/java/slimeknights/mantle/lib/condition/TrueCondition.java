package slimeknights.mantle.lib.condition;

import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.resource.conditions.v1.ConditionJsonProvider;
import net.minecraft.resources.ResourceLocation;

public class TrueCondition implements ConditionJsonProvider {

  public static final TrueCondition INSTANCE = new TrueCondition();

  @Override
  public ResourceLocation getConditionId() {
    return MantleConditions.TRUE;
  }

  @Override
  public void writeParameters(JsonObject object) { }
}
