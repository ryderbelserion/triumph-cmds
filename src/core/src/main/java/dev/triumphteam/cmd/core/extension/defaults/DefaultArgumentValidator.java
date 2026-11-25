package dev.triumphteam.cmd.core.extension.defaults;

import dev.triumphteam.cmd.core.argument.InternalArgument;
import dev.triumphteam.cmd.core.argument.LimitlessInternalArgument;
import dev.triumphteam.cmd.core.argument.UnknownInternalArgument;
import dev.triumphteam.cmd.core.extension.ValidationResult;
import dev.triumphteam.cmd.core.extension.argument.ArgumentValidator;
import dev.triumphteam.cmd.core.extension.meta.CommandMeta;
import org.jspecify.annotations.NonNull;

public class DefaultArgumentValidator<S, ST> implements ArgumentValidator<S, ST> {

    private final boolean validateOptionals;
    private final boolean validateLimitless;

    public DefaultArgumentValidator() {
        this(true, true);
    }

    public DefaultArgumentValidator(final boolean validateOptionals, final boolean validateLimitless) {
        this.validateOptionals = validateOptionals;
        this.validateLimitless = validateLimitless;
    }

    @Override
    public @NonNull ValidationResult<String> validate(
            @NonNull final CommandMeta data,
            @NonNull final InternalArgument<S, ST> argument,
            final int position,
            final int last
    ) {
        // Validation for optionals
        if (this.validateOptionals && (position != last && argument.isOptional())) {
            return invalid("Optional internalArgument is only allowed as the last internalArgument");
        }

        // Validation for limitless
        if (this.validateLimitless && (position != last && argument instanceof LimitlessInternalArgument)) {
            return invalid("Limitless internalArgument is only allowed as the last internalArgument");
        }

        // Unknown types by default throw
        if (argument instanceof UnknownInternalArgument) {
            return invalid("No internalArgument of type \"" + argument.getType().getName() + "\" registered");
        }

        return valid();
    }
}