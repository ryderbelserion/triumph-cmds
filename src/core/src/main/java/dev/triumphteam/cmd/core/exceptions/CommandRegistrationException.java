package dev.triumphteam.cmd.core.exceptions;

import org.jspecify.annotations.NonNull;
import java.lang.reflect.AnnotatedElement;

/**
 * Throws when command registration fails.
 */
public final class CommandRegistrationException extends RuntimeException {

    public CommandRegistrationException(@NonNull final String message) {
        super(message);
    }

    public CommandRegistrationException(
            @NonNull final String message,
            @NonNull final Class<?> commandClass
    ) {
        super(message + ". In Class \"" + commandClass.getName() + "\"");
    }

    public CommandRegistrationException(
            @NonNull final String message,
            @NonNull final AnnotatedElement element,
            @NonNull final Class<?> commandClass
    ) {
        super(message + ". In Method \"" + element + "\" in Class \"" + commandClass.getName() + "\"");
    }
}