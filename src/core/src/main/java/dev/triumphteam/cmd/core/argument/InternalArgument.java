package dev.triumphteam.cmd.core.argument;

import dev.triumphteam.cmd.core.command.ArgumentInput;
import dev.triumphteam.cmd.core.extension.InternalArgumentResult;
import dev.triumphteam.cmd.core.extension.meta.CommandMeta;
import dev.triumphteam.cmd.core.extension.meta.CommandMetaContainer;
import dev.triumphteam.cmd.core.message.context.InvalidArgumentContext;
import dev.triumphteam.cmd.core.suggestion.InternalSuggestion;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * Command argument.
 *
 * @param <S> The sender type.
 */
public interface InternalArgument<S, ST> extends CommandMetaContainer {

    static InternalArgumentResult valid(@NonNull final Object value) {
        return new InternalArgumentResult.Valid(value);
    }

    static InternalArgumentResult invalid(@NonNull final BiFunction<@NonNull CommandMeta, @NonNull String, @NonNull InvalidArgumentContext> context) {
        return new InternalArgumentResult.Invalid(context);
    }

    /**
     * Gets the name of the argument.
     * This will be either the parameter name or <code>arg1</code>, <code>arg2</code>, etc.
     * Needs to be compiled with compiler argument <code>-parameters</code> to show actual names.
     *
     * @return The argument name.
     */
    @NonNull String getName();

    /**
     * The description of this Argument.
     * Holds the description.
     *
     * @return The description of this Argument.
     */
    @NonNull String getDescription();

    /**
     * The argument type.
     * Holds the class type of the argument.
     *
     * @return The argument type.
     */
    @NonNull Class<?> getType();

    /**
     * If the argument is optional or not.
     *
     * @return Whether the argument is optional.
     */
    boolean isOptional();

    @Nullable String getDefaultValue();

    boolean canSuggest();

    @NonNull InternalArgumentResult resolve(@NonNull final S sender, @NonNull final ArgumentInput input);

    @NonNull List<ST> suggestions(
            @NonNull final S sender,
            @NonNull final String current,
            @NonNull final List<String> arguments,
            @NonNull final Map<String, String> argumentsMap
    );

    @NonNull InternalSuggestion<S, ST> getSuggestion();

    @FunctionalInterface
    interface Factory<S, ST> {

        @NonNull StringInternalArgument<S, ST> create(
                @NonNull final CommandMeta meta,
                @NonNull final String name,
                @NonNull final String description,
                @NonNull final Class<?> type,
                @NonNull final InternalSuggestion<S, ST> suggestion,
                final boolean optional
        );
    }
}