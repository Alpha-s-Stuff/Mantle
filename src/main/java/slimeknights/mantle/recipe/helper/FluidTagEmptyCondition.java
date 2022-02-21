//package slimeknights.mantle.recipe.helper;
//
//import com.google.gson.JsonObject;
//import lombok.RequiredArgsConstructor;
//import net.fabricmc.fabric.api.resource.conditions.v1.ConditionJsonProvider;
//import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditions;
//import net.minecraft.core.Registry;
//import net.minecraft.resources.ResourceLocation;
//import net.minecraft.tags.SerializationTags;
//import net.minecraft.tags.Tag;
//import net.minecraft.world.level.material.Fluid;
//import net.minecraftforge.common.crafting.conditions.ICondition;
//import net.minecraftforge.common.crafting.conditions.IConditionSerializer;
//import slimeknights.mantle.Mantle;
//import slimeknights.mantle.util.JsonHelper;
//
///** Condition that checks when a fluid tag is empty. Same as {@link net.minecraftforge.common.crafting.conditions.TagEmptyCondition} but for fluids instead of items */
//@RequiredArgsConstructor
//public class FluidTagEmptyCondition implements ConditionJsonProvider {
//  private static final ResourceLocation NAME = Mantle.getResource("fluid_tag_empty");
//  private final ResourceLocation name;
//
//  public FluidTagEmptyCondition(String domain, String name) {
//    this(new ResourceLocation(domain, name));
//  }
//
//  @Override
//  public ResourceLocation getConditionId() {
//    return NAME;
//  }
//
//  @Override
//  public boolean test() {
//    Tag<Fluid> tag = SerializationTags.getInstance().getOrEmpty(Registry.FLUID_REGISTRY).getTag(name);
//    return tag == null || tag.getValues().isEmpty();
//  }
//
//  @Override
//  public void writeParameters(JsonObject json) {
//    json.addProperty("tag", name.toString());
//  }
//
//  @Override
//  public String toString()
//  {
//    return "fluid_tag_empty(\"" + name + "\")";
//  }
//}
