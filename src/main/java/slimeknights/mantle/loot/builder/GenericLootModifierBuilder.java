package slimeknights.mantle.loot.builder;

import lombok.RequiredArgsConstructor;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import slimeknights.mantle.data.GlobalLootModifierProvider;
import io.github.fabricators_of_create.porting_lib.loot.GlobalLootModifierSerializer;
import io.github.fabricators_of_create.porting_lib.loot.LootModifier;

import java.util.function.Function;

/** Generic instantiation of the GLM builder */
@RequiredArgsConstructor(staticName = "builder")
public class GenericLootModifierBuilder<T extends LootModifier> extends AbstractLootModifierBuilder<GenericLootModifierBuilder<T>> {
  /** Serializer instance for this loot modifier */
  private final GlobalLootModifierSerializer<T> serializer;
  /** Constructor for the loot modifier */
  private final Function<LootItemCondition[],T> constructor;

  @Override
  public void build(String name, GlobalLootModifierProvider provider) {
    provider.add(name, serializer, constructor.apply(getConditions()));
  }
}
