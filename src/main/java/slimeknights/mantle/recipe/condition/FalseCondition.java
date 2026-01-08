package slimeknights.mantle.recipe.condition;

import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.resource.conditions.v1.ConditionJsonProvider;
import net.minecraft.resources.ResourceLocation;
import slimeknights.mantle.Mantle;

public final class FalseCondition implements ConditionJsonProvider {
  public static final FalseCondition INSTANCE = new FalseCondition();
  public static final ResourceLocation ID = Mantle.getResource("false");

  private FalseCondition() {}

  @Override
  public ResourceLocation getConditionId() {
    return ID;
  }

  @Override
  public void writeParameters(JsonObject object) {}

  public static boolean test(JsonObject json) {
    return false;
  }
}
