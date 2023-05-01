package slimeknights.mantle.loot.builder;

import com.mojang.serialization.Codec;
import io.github.fabricators_of_create.porting_lib.loot.GlobalLootModifierProvider;
import lombok.RequiredArgsConstructor;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import io.github.fabricators_of_create.porting_lib.loot.LootModifier;

import java.util.function.Function;

/** Generic instantiation of the GLM builder */
@RequiredArgsConstructor(staticName = "builder")
public class GenericLootModifierBuilder<T extends LootModifier> extends AbstractLootModifierBuilder<GenericLootModifierBuilder<T>> {
  /** Constructor for the loot modifier */
  private final Function<LootItemCondition[],T> constructor;

  @Override
  public void build(String name, GlobalLootModifierProvider provider) {
    provider.add(name, constructor.apply(getConditions()));
  }
}
