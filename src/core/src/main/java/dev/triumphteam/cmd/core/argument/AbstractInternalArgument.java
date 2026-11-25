package dev.triumphteam.cmd.core.argument;

import dev.triumphteam.cmd.core.extension.meta.CommandMeta;
import dev.triumphteam.cmd.core.suggestion.EmptySuggestion;
import dev.triumphteam.cmd.core.suggestion.InternalSuggestion;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Command internalArgument.
 * Which is divided into {@link StringInternalArgument} and {@link LimitlessInternalArgument}.
 *
 * @param <S>  The sender type.
 * @param <ST> The suggestion type.
 */
public abstract class AbstractInternalArgument<S, ST> implements InternalArgument<S, ST> {

    private final CommandMeta meta;

    private final String name;
    private final String description;
    private final Class<?> type;
    private final String defaultValue;
    private final boolean optional;
    private final InternalSuggestion<S, ST> suggestion;

    public AbstractInternalArgument(
            @NonNull final CommandMeta meta,
            @NonNull final String name,
            @NonNull final String description,
            @NonNull final Class<?> type,
            @NonNull final InternalSuggestion<S, ST> suggestion,
            @Nullable final String defaultValue,
            final boolean optional
    ) {
        this.meta = meta;
        this.name = name;
        this.description = description;
        this.type = type;
        this.suggestion = suggestion;
        this.defaultValue = defaultValue;
        this.optional = optional;
    }

    @Override
    public @NonNull List<ST> suggestions(
            @NonNull final S sender,
            @NonNull final String current,
            @NonNull final List<String> arguments,
            @NonNull final Map<String, String> argumentsMap
    ) {
        if (!(this.suggestion instanceof InternalSuggestion.Simple<S, ST> result)) return Collections.emptyList();

        return result.getSuggestions(sender, current, arguments, argumentsMap);
    }

    @Override
    public @NonNull CommandMeta getMeta() {
        return this.meta;
    }

    /**
     * Gets the name of the internalArgument.
     * This will be either the parameter name or <code>arg1</code>, <code>arg2</code>, etc.
     * Needs to be compiled with compiler internalArgument <code>-parameters</code> to show actual names.
     *
     * @return The internalArgument name.
     */
    @Override
    public @NonNull String getName() {
        return this.name;
    }

    @Override
    public @NonNull String getDescription() {
        return this.description;
    }

    /**
     * The internalArgument type.
     * Holds the class type of the internalArgument.
     *
     * @return The internalArgument type.
     */
    @Override
    public @NonNull Class<?> getType() {
        return this.type;
    }

    /**
     * If internalArgument is optional or not.
     *
     * @return Whether the internalArgument is optional.
     */
    @Override
    public boolean isOptional() {
        return this.optional;
    }

    @Override
    public @Nullable String getDefaultValue() {
        return this.defaultValue;
    }

    @Override
    public boolean canSuggest() {
        return !(this.suggestion instanceof EmptySuggestion);
    }

    @Override
    public @NonNull InternalSuggestion<S, ST> getSuggestion() {
        return this.suggestion;
    }

    @Override
    public @NonNull String toString() {
        return "AbstractInternalArgument{" +
                "name='" + this.name + '\'' +
                ", description='" + this.description + '\'' +
                ", type=" + this.type +
                ", optional=" + this.optional +
                ", suggestion=" + this.suggestion +
                '}';
    }
}