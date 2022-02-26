package slimeknights.mantle.lib.conditons;

import net.fabricmc.fabric.api.resource.conditions.v1.ConditionJsonProvider;

import java.util.ArrayList;
import java.util.List;

public class ConditionBuilder {
  public static ConditionBuilder builder() {
    return new ConditionBuilder();
  }

  private final List<ConditionJsonProvider> conditions = new ArrayList<>();

  public ConditionBuilder addCondition(ConditionJsonProvider... condition) {
    conditions.addAll(List.of(condition));
    return this;
  }
}
