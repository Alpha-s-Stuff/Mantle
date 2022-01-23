package slimeknights.mantle.lib.condition;

import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditions;
import net.minecraft.resources.ResourceLocation;

public class MantleConditions {

  public static ResourceLocation TRUE = new ResourceLocation("c", "true");

  public static void init() {
    ResourceConditions.register(TRUE, jsonObject -> true);
  }
}
