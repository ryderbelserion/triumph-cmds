package dev.triumphteam.cmd.core.extension.registry;

import dev.triumphteam.cmd.core.requirement.RequirementKey;
import dev.triumphteam.cmd.core.requirement.RequirementResolver;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * Registry used for registering new requirements for all commands to use.
 *
 * @param <S> The sender type.
 */
public final class RequirementRegistry<D, S> implements Registry {

    private final Map<RequirementKey, RequirementResolver<D, S>> requirements = new HashMap<>();

    /**
     * Registers a new {@link RequirementResolver} for the specific Key.
     *
     * @param key      The requirement key.
     * @param resolver The resolver to check if the requirement is met.
     */
    public void register(@NonNull final RequirementKey key, @NonNull final RequirementResolver<D, S> resolver) {
        this.requirements.put(key, resolver);
    }

    /**
     * Gets the {@link RequirementResolver} for the specific Key.
     *
     * @param key The specific key.
     * @return A saved {@link RequirementResolver}.
     */
    public @Nullable RequirementResolver<D, S> getRequirement(@NonNull final RequirementKey key) {
        return this.requirements.get(key);
    }
}