package dev.triumphteam.cmd.core.message.context;

import dev.triumphteam.cmd.core.extension.meta.CommandMeta;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * Context for when user types an invalid argument based on its type.
 */
public final class InvalidCommandContext extends InvalidInputContext {

    public InvalidCommandContext(
            @NonNull final CommandMeta meta,
            @NonNull final String syntax,
            @Nullable final String invalidInput
    ) {
        super(meta, syntax, invalidInput);
    }
}