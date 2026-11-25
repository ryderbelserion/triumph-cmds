package dev.triumphteam.cmd.core.message.context;

import dev.triumphteam.cmd.core.extension.meta.CommandMeta;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * Context for when a user types an invalid argument based on its type.
 */
public final class InvalidArgumentContext extends InvalidInputContext {

    private final String name;
    private final Class<?> type;

    public InvalidArgumentContext(
            @NonNull final CommandMeta meta,
            @NonNull final String syntax,
            @Nullable final String invalidInput,
            @NonNull final String name,
            @NonNull final Class<?> type
    ) {
        super(meta, syntax, invalidInput);

        this.name = name;
        this.type = type;
    }

    public @NonNull String getArgumentName() {
        return this.name;
    }

    public @NonNull Class<?> getArgumentType() {
        return this.type;
    }
}