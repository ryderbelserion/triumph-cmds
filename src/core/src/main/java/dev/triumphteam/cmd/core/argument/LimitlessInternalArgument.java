package dev.triumphteam.cmd.core.argument;

import dev.triumphteam.cmd.core.extension.meta.CommandMeta;
import dev.triumphteam.cmd.core.suggestion.InternalSuggestion;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * A limitless internalArgument is an internalArgument type that won't check for internalArgument size.
 * For example, Lists, Arrays, etc.
 *
 * @param <S> The sender type.
 */
public abstract class LimitlessInternalArgument<S, ST> extends AbstractInternalArgument<S, ST> {

    public LimitlessInternalArgument(
            @NonNull final CommandMeta meta,
            @NonNull final String name,
            @NonNull final String description,
            @NonNull final Class<?> type,
            @NonNull final InternalSuggestion<S, ST> suggestion,
            @Nullable final String defaultValue,
            final boolean isOptional
    ) {
        super(meta, name, description, type, suggestion, defaultValue, isOptional);
    }

    @Override
    public @NonNull String toString() {
        return "LimitlessArgument{super=" + super.toString() + "}";
    }
}