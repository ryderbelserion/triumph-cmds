package dev.triumphteam.cmd.core.message.context;

import dev.triumphteam.cmd.core.extension.meta.CommandMeta;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * Context with an invalid input.
 */
abstract class InvalidInputContext extends SyntaxMessageContext {

    private final String invalidInput;

    public InvalidInputContext(
            @NonNull final CommandMeta meta,
            @NonNull final String syntax,
            @Nullable final String invalidInput
    ) {
        super(meta, syntax);

        this.invalidInput = invalidInput == null ? "" : invalidInput;
    }

    public @NonNull String getInvalidInput() {
        return this.invalidInput;
    }
}