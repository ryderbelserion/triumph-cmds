/**
 * MIT License
 * <p>
 * Copyright (c) 2019-2021 Matt
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package dev.triumphteam.cmd.core.argument;

import dev.triumphteam.cmd.core.extension.Result;
import dev.triumphteam.cmd.core.extension.meta.CommandMeta;
import dev.triumphteam.cmd.core.extension.meta.CommandMetaContainer;
import dev.triumphteam.cmd.core.message.context.InvalidArgumentContext;
import dev.triumphteam.cmd.core.suggestion.Suggestion;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.Deque;
import java.util.List;
import java.util.function.BiFunction;

/**
 * Command argument.
 *
 * @param <S> the sender type.
 * @param <T> the Argument type.
 */
public interface InternalArgument<S, T> extends CommandMetaContainer {

    /**
     * Gets the name of the argument.
     * This will be either the parameter name or <code>arg1</code>, <code>arg2</code>, etc.
     * Needs to be compiled with compiler argument <code>-parameters</code> to show actual names.
     *
     * @return the argument name.
     */
    @NotNull String getName();

    /**
     * The description of this Argument.
     * Holds the description.
     *
     * @return the description of this Argument.
     */
    @NotNull String getDescription();

    /**
     * The argument type.
     * Holds the class type of the argument.
     *
     * @return the argument type.
     */
    @NotNull Class<?> getType();

    /**
     * If argument is optional or not.
     *
     * @return whether the argument is optional.
     */
    boolean isOptional();

    boolean canSuggest();

    /**
     * Resolves the argument type.
     *
     * @param sender the sender to resolve to.
     * @param value the argument value.
     * @param provided a provided value by a platform in case parsing isn't needed.
     * @return a resolve {@link Result}.
     */
    @NotNull Result<@Nullable Object, BiFunction<@NotNull CommandMeta, @NotNull String, @NotNull InvalidArgumentContext>> resolve(
            @NotNull final S sender,
            @NotNull final T value,
            @Nullable final Object provided
    );

    /**
     * Resolves the argument type.
     *
     * @param sender the sender to resolve to.
     * @param value the argument value.
     * @return a resolve {@link Result}.
     */
    default @NotNull Result<@Nullable Object, BiFunction<@NotNull CommandMeta, @NotNull String, @NotNull InvalidArgumentContext>> resolve(
            @NotNull final S sender,
            @NotNull final T value
    ) {
        return resolve(sender, value, null);
    }

    /**
     * Create a list of suggestion strings to return to the platform requesting it.
     *
     * @param sender the sender to get suggestions for.
     * @param arguments the arguments used in the suggestion.
     * @return a list of valid suggestions for the argument.
     */
    @NotNull List<String> suggestions(@NotNull final S sender, @NotNull final Deque<String> arguments);

    static Result<@Nullable Object, BiFunction<@NotNull CommandMeta, @NotNull String, @NotNull InvalidArgumentContext>> success(
            @NotNull final Object value
    ) {
        return new Result.Success<>(value);
    }

    static Result<@Nullable Object, BiFunction<@NotNull CommandMeta, @NotNull String, @NotNull InvalidArgumentContext>> invalid(
            @NotNull final BiFunction<@NotNull CommandMeta, @NotNull String, @NotNull InvalidArgumentContext> context
    ) {
        return new Result.Failure<>(context);
    }

    @FunctionalInterface
    interface Factory<S> {

        @NotNull StringInternalArgument<S> create(
                @NotNull final CommandMeta meta,
                @NotNull final String name,
                @NotNull final String description,
                @NotNull final Class<?> type,
                @NotNull final Suggestion<S> suggestion,
                final boolean optional
        );
    }
}