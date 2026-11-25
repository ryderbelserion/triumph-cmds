package dev.triumphteam.cmd.core.argument.keyed;

import dev.triumphteam.cmd.core.extension.StringKey;
import org.jspecify.annotations.NonNull;

/**
 * Identifier for a list of registered named arguments.
 */
public class ArgumentKey extends StringKey {

    private ArgumentKey(@NonNull final String key) {
        super(key);
    }

    /**
     * Factory method for creating a {@link ArgumentKey}.
     *
     * @param key The value of the key, normally separated by <code>.</code>.
     * @return A new {@link ArgumentKey}.
     */
    public static @NonNull ArgumentKey of(@NonNull final String key) {
        return new ArgumentKey(key);
    }

    @Override
    public @NonNull String toString() {
        return "ArgumentKey{super=" + super.toString() + "}";
    }
}