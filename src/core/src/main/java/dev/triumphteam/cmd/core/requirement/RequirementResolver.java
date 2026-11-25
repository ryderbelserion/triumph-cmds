package dev.triumphteam.cmd.core.requirement;

import org.jspecify.annotations.NonNull;

/**
 * Functional interface to allow simple requirement registering without the use of any hard coded data.
 *
 * @param <D> The default sender type.
 * @param <S> The command sender type.
 */
@FunctionalInterface
public interface RequirementResolver<D, S> {

    /**
     * Resolves the requirement.
     *
     * @param sender The sender to check the requirement.
     * @return Whether the requirement passes or fails.
     */
    boolean resolve(
            @NonNull final S sender,
            @NonNull final RequirementContext<D, S> context
    );
}