package dev.triumphteam.cmd.core.argument;

import dev.triumphteam.cmd.core.command.ArgumentInput;
import dev.triumphteam.cmd.core.extension.InternalArgumentResult;
import dev.triumphteam.cmd.core.extension.meta.CommandMeta;
import dev.triumphteam.cmd.core.message.context.InvalidArgumentContext;
import dev.triumphteam.cmd.core.suggestion.EmptySuggestion;
import org.jspecify.annotations.NonNull;

/**
 * Represents an internal argument that provides a default behavior for unknown or
 * unsupported types of arguments during processing.
 * This will always throw an exception when trying to process the command.
 *
 * @param <S> The type of the sender.
 */
public final class UnknownInternalArgument<S, ST> extends StringInternalArgument<S, ST> {

    public UnknownInternalArgument(@NonNull final Class<?> type) {
        super(new CommandMeta.Builder(null).build(), "unknown", "unknown.", type, new EmptySuggestion<>(), null, false);
    }

    @Override
    public @NonNull InternalArgumentResult resolve(@NonNull final S sender, @NonNull final ArgumentInput input) {
        return InternalArgument.invalid((meta, syntax) -> new InvalidArgumentContext(meta, syntax, "", "", Void.TYPE));
    }

    @Override
    public @NonNull String toString() {
        return "UnknownInternalArgument{} " + super.toString();
    }
}