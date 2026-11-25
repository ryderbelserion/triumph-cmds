package dev.triumphteam.cmd.core.extension.argument;

import dev.triumphteam.cmd.core.argument.InternalArgument;
import dev.triumphteam.cmd.core.extension.ValidationResult;
import dev.triumphteam.cmd.core.extension.meta.CommandMeta;
import org.jspecify.annotations.NonNull;

public interface ArgumentValidator<S, ST> {

    @NonNull ValidationResult<String> validate(
            @NonNull final CommandMeta meta,
            @NonNull final InternalArgument<S, ST> argument,
            final int position,
            final int last
    );

    default @NonNull ValidationResult<String> invalid(@NonNull final String message) {
        return new ValidationResult.Invalid<>(message);
    }

    default @NonNull ValidationResult<String> valid() {
        return new ValidationResult.Valid<>();
    }

    default @NonNull ValidationResult<String> ignore() {
        return new ValidationResult.Ignore<>();
    }
}