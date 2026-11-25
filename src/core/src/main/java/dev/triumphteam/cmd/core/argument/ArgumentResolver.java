package dev.triumphteam.cmd.core.argument;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * Resolver for custom argument types.
 *
 * @param <S> The sender type.
 */
@FunctionalInterface
public interface ArgumentResolver<S> {

    /**
     * Resolves the current argument based on the given argument.
     *
     * @param sender The sender which can be used for checks.
     * @param argument    The typed argument.
     * @return An Object with the resolved value or null.
     */
    @Nullable Object resolve(@NonNull final S sender, @NonNull final String argument);
}