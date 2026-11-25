package dev.triumphteam.cmd.core.suggestion;

import dev.triumphteam.cmd.core.extension.StringKey;
import org.jspecify.annotations.NonNull;

/**
 * Key used to identify the {@link SuggestionResolver} in the {@link SuggestionRegistry}.
 */
public final class SuggestionKey extends StringKey {

    private SuggestionKey(@NonNull final String key) {
        super(key);
    }

    /**
     * Factory method for creating a {@link SuggestionKey}.
     *
     * @param key The value of the key, normally separated by <code>.</code>.
     * @return A new {@link SuggestionKey}.
     */
    public static @NonNull SuggestionKey of(@NonNull final String key) {
        return new SuggestionKey(key);
    }

    @Override
    public @NonNull String toString() {
        return "SuggestionKey{super=" + super.toString() + "}";
    }
}