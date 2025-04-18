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
import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;

/**
 * Joined string argument, a {@link LimitlessInternalArgument}.
 * Returns a single {@link String} that was joined from a {@link List} of arguments.
 *
 * @param <S> the sender type.
 */
public final class JoinedStringInternalArgument<S> extends LimitlessInternalArgument<S> {

    private final CharSequence delimiter;

    public JoinedStringInternalArgument(
            @NotNull final CommandMeta meta,
            @NotNull final String name,
            @NotNull final String description,
            @NotNull final CharSequence delimiter,
            @NotNull final Suggestion<S> suggestion,
            final boolean optional
    ) {
        super(meta, name, description, String.class, suggestion, optional);
        this.delimiter = delimiter;
    }

    /**
     * Resolves the argument type.
     *
     * @param sender the sender to resolve to.
     * @param value the arguments {@link List}.
     * @return a single {@link String} with the joined {@link List}.
     */
    @Override
    public @NotNull Result<@Nullable Object, BiFunction<@NotNull CommandMeta, @NotNull String, @NotNull InvalidArgumentContext>> resolve(
            @NotNull final S sender,
            @NotNull final Collection<String> value,
            @Nullable final Object provided
    ) {
        return InternalArgument.success(String.join(delimiter, value));
    }

    @Override
    public @NotNull String toString() {
        return "JoinedStringArgument{" +
                "delimiter=" + this.delimiter +
                ", super=" + super.toString() + "}";
    }
}