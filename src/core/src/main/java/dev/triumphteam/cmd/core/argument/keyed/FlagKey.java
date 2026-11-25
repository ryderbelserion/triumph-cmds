package dev.triumphteam.cmd.core.argument.keyed;

import dev.triumphteam.cmd.core.extension.StringKey;
import org.jspecify.annotations.NonNull;

/**
 * Identifier for a list of registered flags.
 */
public final class FlagKey extends StringKey {

    private FlagKey(@NonNull final String key) {
        super(key);
    }

    /**
     * Factory method for creating a {@link FlagKey}.
     *
     * @param key The value of the key, normally separated by <code>.</code>.
     * @return A new {@link FlagKey}.
     */
    public static @NonNull FlagKey of(@NonNull final String key) {
        return new FlagKey(key);
    }

    @Override
    public @NonNull String toString() {
        return "FlagKey{super=" + super.toString() + "}";
    }
}