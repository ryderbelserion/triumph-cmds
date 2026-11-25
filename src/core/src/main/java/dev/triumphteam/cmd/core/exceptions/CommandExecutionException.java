package dev.triumphteam.cmd.core.exceptions;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * Throws when the command execution fails.
 */
public final class CommandExecutionException extends RuntimeException {

    public CommandExecutionException(@NonNull final String message) {
        super(message);
    }

    public CommandExecutionException(
            @NonNull final String message,
            @NonNull final String parent,
            @NonNull final String command
    ) {
        super(message + ". On \"" + parent + "\" command on \"" + command + "\" sub command.");
    }

    @Override
    public synchronized @NonNull CommandExecutionException initCause(@Nullable final Throwable cause) {
        super.initCause(cause);

        return this;
    }
}
