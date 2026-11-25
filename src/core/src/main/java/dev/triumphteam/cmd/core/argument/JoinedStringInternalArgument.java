package dev.triumphteam.cmd.core.argument;

import dev.triumphteam.cmd.core.command.ArgumentInput;
import dev.triumphteam.cmd.core.extension.InternalArgumentResult;
import dev.triumphteam.cmd.core.extension.meta.CommandMeta;
import dev.triumphteam.cmd.core.suggestion.InternalSuggestion;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import java.util.List;

/**
 * Joined string argument, a {@link LimitlessInternalArgument}.
 * Returns a single {@link String} that was joined from a {@link List} of arguments.
 *
 * @param <S> The sender type.
 */
public final class JoinedStringInternalArgument<S, ST> extends LimitlessInternalArgument<S, ST> {

    private final CharSequence delimiter;

    public JoinedStringInternalArgument(
            @NonNull final CommandMeta meta,
            @NonNull final String name,
            @NonNull final String description,
            @NonNull final CharSequence delimiter,
            @NonNull final InternalSuggestion<S, ST> suggestion,
            @Nullable final String defaultValue,
            final boolean optional
    ) {
        super(meta, name, description, String.class, suggestion, defaultValue, optional);

        this.delimiter = delimiter;
    }

    @Override
    public @NonNull InternalArgumentResult resolve(@NonNull final S sender, @NonNull final ArgumentInput input) {
        return InternalArgument.valid(String.join(this.delimiter, input.input()));
    }

    @Override
    public @NonNull String toString() {
        return "JoinedStringArgument{" +
                "delimiter=" + this.delimiter +
                ", super=" + super.toString() + "}";
    }
}