package dev.triumphteam.cmd.core.argument;

import dev.triumphteam.cmd.core.command.ArgumentInput;
import dev.triumphteam.cmd.core.extension.InternalArgumentResult;
import dev.triumphteam.cmd.core.extension.meta.CommandMeta;
import dev.triumphteam.cmd.core.extension.registry.ArgumentRegistry;
import dev.triumphteam.cmd.core.message.context.InvalidArgumentContext;
import dev.triumphteam.cmd.core.suggestion.InternalSuggestion;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * Normal {@link StringInternalArgument}.
 * Basically the main implementation.
 * Uses an {@link ArgumentResolver} from the {@link ArgumentRegistry}.
 * Allows you to register many other simple argument types.
 *
 * @param <S> The sender type.
 */
public final class ResolverInternalArgument<S, ST> extends StringInternalArgument<S, ST> {

    private final ArgumentResolver<S> resolver;

    public ResolverInternalArgument(
            @NonNull final CommandMeta meta,
            @NonNull final String name,
            @NonNull final String description,
            @NonNull final Class<?> type,
            @NonNull final ArgumentResolver<S> resolver,
            @NonNull final InternalSuggestion<S, ST> suggestion,
            @Nullable final String defaultValue,
            final boolean optional
    ) {
        super(meta, name, description, type, suggestion, defaultValue, optional);

        this.resolver = resolver;
    }

    @Override
    public @NonNull InternalArgumentResult resolve(@NonNull final S sender, @NonNull final ArgumentInput input) {
        final String value = input.input();

        if (!canUseInput(value)) {
            return InternalArgument.invalid((commands, syntax) -> new InvalidArgumentContext(commands, syntax, value, getName(), getType()));
        }

        final Object result = this.resolver.resolve(sender, value);

        if (result == null) {
            return InternalArgument.invalid((commands, syntax) -> new InvalidArgumentContext(commands, syntax, value, getName(), getType()));
        }

        return InternalArgument.valid(result);
    }

    @Override
    public @NonNull String toString() {
        return "ResolverArgument{" +
                "resolver=" + this.resolver +
                ", super=" + super.toString() + "}";
    }
}