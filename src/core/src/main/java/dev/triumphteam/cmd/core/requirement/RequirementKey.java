package dev.triumphteam.cmd.core.requirement;

import dev.triumphteam.cmd.core.extension.StringKey;
import dev.triumphteam.cmd.core.extension.registry.RequirementRegistry;
import org.jspecify.annotations.NonNull;

/**
 * Key used to identify the {@link RequirementResolver} in the {@link RequirementRegistry}.
 */
public final class RequirementKey extends StringKey {

    private RequirementKey(@NonNull final String key) {
        super(key);
    }

    /**
     * Factory method for creating a {@link RequirementKey}.
     *
     * @param key The value of the key, normally separated by <code>.</code>.
     * @return A new {@link RequirementKey}.
     */
    public static @NonNull RequirementKey of(@NonNull final String key) {
        return new RequirementKey(key);
    }

    @Override
    public @NonNull String toString() {
        return "RequirementKey{super=" + super.toString() + "}";
    }
}