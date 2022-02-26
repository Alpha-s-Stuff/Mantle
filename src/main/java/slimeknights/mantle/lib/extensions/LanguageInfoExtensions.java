package slimeknights.mantle.lib.extensions;

import java.util.Locale;

public interface LanguageInfoExtensions {
	default Locale mantle$getJavaLocale() {
    return null;
  }
}
