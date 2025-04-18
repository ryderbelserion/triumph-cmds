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
import dev.triumphteam.cmd.core.message.context.InvalidArgumentContext;
import dev.triumphteam.cmd.core.suggestion.Suggestion;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

/**
 * Splitting argument takes a string and splits it into a collection.
 *
 * @param <S> the sender type.
 */
public final class SplitStringInternalArgument<S> extends StringInternalArgument<S> {

    private final String regex;
    private final InternalArgument<S, String> internalArgument;
    private final Class<?> collectionType;

    public SplitStringInternalArgument(
            @NotNull final CommandMeta meta,
            @NotNull final String name,
            @NotNull final String description,
            @NotNull final String regex,
            @NotNull final InternalArgument<S, String> internalArgument,
            @NotNull final Class<?> collectionType,
            @NotNull final Suggestion<S> suggestion,
            final boolean optional
    ) {
        super(meta, name, description, String.class, suggestion, optional);

        this.regex = regex;
        this.internalArgument = internalArgument;
        this.collectionType = collectionType;
    }

    /**
     * Takes a string and splits it into a collection.
     *
     * @param sender the sender to resolve to.
     * @param value the argument value.
     * @return a collection of the split strings.
     */
    @Override
    public @NotNull Result<@Nullable Object, BiFunction<@NotNull CommandMeta, @NotNull String, @NotNull InvalidArgumentContext>> resolve(
            @NotNull final S sender,
            @NotNull final String value,
            @Nullable final Object provided
    ) {
        return CollectionInternalArgument.resolveCollection(sender, internalArgument, Arrays.asList(value.split(regex)), List.class);
    }

    @Override
    public @NotNull List<String> suggestions(
            @NotNull final S sender,
            @NotNull final Deque<String> arguments
    ) {
        final String peek = arguments.peekLast();
        final String last = peek == null ? "" : peek;

        final List<String> split = Arrays.asList(last.split(this.regex));

        if (split.isEmpty()) return Collections.emptyList();

        final String current = last.endsWith(this.regex) ? "" : split.getLast();
        final String joined = String.join(this.regex, current.isEmpty() ? split : split.subList(0, split.size() - 1));
        final String map = joined.isEmpty() ? "" : joined + this.regex;

        return getSuggestion()
                .getSuggestions(sender, current, new ArrayList<>(arguments))
                .stream()
                .map(it -> map + it)
                .collect(Collectors.toList());
    }

    @Override
    public @NotNull String toString() {
        return "SplitArgument{super=" + super.toString() + "}";
    }
}