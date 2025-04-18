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
package dev.triumphteam.cmd.core.argument.keyed;

import dev.triumphteam.cmd.core.util.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@SuppressWarnings("unchecked")
public final class KeyedArguments extends FlagsContainer {

    private final Map<String, ArgumentValue> values;
    private final List<String> nonTokens;

    public KeyedArguments(
            @NotNull final Map<String, ArgumentValue> values,
            @NotNull final Map<String, ArgumentValue> flags,
            @NotNull final List<String> nonTokens
    ) {
        super(flags);

        this.values = values;
        this.nonTokens = nonTokens;
    }

    @Override
    public <T> @NotNull Optional<T> getArgument(@NotNull final String name, @NotNull final Class<T> type) {
        return Optional.ofNullable((T) getValue(name));
    }

    @Override
    public <T> @NotNull Optional<List<T>> getListArgument(@NotNull final String name, @NotNull final Class<T> type) {
        final List<T> value = (List<T>) getValue(name);

        return Optional.ofNullable(value);
    }

    @Override
    public <T> @NotNull Optional<Set<T>> getSetArgument(@NotNull final String name, @NotNull final Class<T> type) {
        final Set<T> value = (Set<T>) getValue(name);

        return Optional.ofNullable(value);
    }

    private @Nullable Object getValue(@NotNull final String name) {
        final ArgumentValue argumentValue = this.values.get(name);

        if (argumentValue == null) return null;

        if (argumentValue instanceof SimpleArgumentValue) {
            return ((SimpleArgumentValue) argumentValue).getValue();
        }

        return null;
    }

    @Override
    public @NotNull Map<String, Object> getAllArguments() {
        return this.values.entrySet().stream().map(entry -> {
            final ArgumentValue value = entry.getValue();

            if (value instanceof SimpleArgumentValue) {
                return new Pair<>(entry.getKey(), ((SimpleArgumentValue) value).getValue());
            }

            return new Pair<>(entry.getKey(), null);
        }).collect(Collectors.toMap(Pair::first, Pair::second));
    }

    @Override
    public @NotNull String getText() {
        return getText(" ");
    }

    @Override
    public @NotNull String getText(@NotNull final String delimiter) {
        return String.join(delimiter, this.nonTokens);
    }

    @Override
    public boolean hasArguments() {
        return !this.values.isEmpty();
    }

    @Override
    public @NotNull String toString() {
        return "Arguments{" +
                "values=" + this.values +
                ", super=" + super.toString() + "}";
    }
}