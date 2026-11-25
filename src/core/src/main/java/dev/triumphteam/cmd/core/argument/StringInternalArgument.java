package dev.triumphteam.cmd.core.argument;

import dev.triumphteam.cmd.core.extension.meta.CommandMeta;
import dev.triumphteam.cmd.core.suggestion.InternalSuggestion;
import dev.triumphteam.cmd.core.suggestion.StaticSuggestion;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * Differently from the {@link LimitlessInternalArgument}, this internalArgument will always be just one string as the arg value.
 * And will return one single value as the resolved value.
 *
 * @param <S> The sender type.
 */
public abstract class StringInternalArgument<S, ST> extends AbstractInternalArgument<S, ST> {

    public StringInternalArgument(
            @NonNull final CommandMeta meta,
            @NonNull final String name,
            @NonNull final String description,
            @NonNull final Class<?> type,
            @NonNull final InternalSuggestion<S, ST> suggestion,
            @Nullable final String defaultValue,
            final boolean optional
    ) {
        super(meta, name, description, type, suggestion, defaultValue, optional);
    }

    protected boolean canUseInput(@NonNull final String input) {
        final InternalSuggestion<S, ST> suggestion = getSuggestion();

        if (!(suggestion instanceof StaticSuggestion<S, ST> staticSuggestion)) return true;

        return staticSuggestion.contains(input);
    }

    @Override
    public @NonNull String toString() {
        return "StringArgument{super=" + super.toString() + "}";
    }
}