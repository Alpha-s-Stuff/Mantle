package slimeknights.mantle.lib.util;

import com.tterrag.registrate.util.nullness.NonNullConsumer;
import java.util.Set;

public interface ListenerProvider {
	Set<NonNullConsumer> getListeners();
	default void addListener(NonNullConsumer listener) {
		getListeners().add(listener);
	}

	default void invalidate() {
		getListeners().forEach(listener -> listener.accept(this));
	}
}
