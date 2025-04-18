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
import dev.triumphteam.cmd.core.extension.registry.ArgumentRegistry;
import dev.triumphteam.cmd.core.message.context.InvalidArgumentContext;
import dev.triumphteam.cmd.core.suggestion.Suggestion;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.function.BiFunction;

/**
 * Normal {@link StringInternalArgument}.
 * Basically the main implementation.
 * Uses an {@link ArgumentResolver} from the {@link ArgumentRegistry}.
 * Allows you to register many other simple argument types.
 *
 * @param <S> the sender type.
 */
public final class ResolverInternalArgument<S> extends StringInternalArgument<S> {

    private final ArgumentResolver<S> resolver;

    public ResolverInternalArgument(
            @NotNull final CommandMeta meta,
            @NotNull final String name,
            @NotNull final String description,
            @NotNull final Class<?> type,
            @NotNull final ArgumentResolver<S> resolver,
            @NotNull final Suggestion<S> suggestion,
            final boolean optional
    ) {
        super(meta, name, description, type, suggestion, optional);

        this.resolver = resolver;
    }

    /**
     * Resolves the argument type.
     *
     * @param sender the sender to resolve to.
     * @param value the {@link String} argument value.
     * @return an Object value of the correct type, based on the result from the {@link ArgumentResolver}.
     */
    @Override
    public @NotNull Result<@Nullable Object, BiFunction<@NotNull CommandMeta, @NotNull String, @NotNull InvalidArgumentContext>> resolve(
            @NotNull final S sender,
            @NotNull final String value,
            @Nullable final Object provided
    ) {
        final Object result = this.resolver.resolve(sender, value);

        if (result == null) {
            return InternalArgument.invalid((commands, arguments) -> new InvalidArgumentContext(commands, arguments, value, getName(), getType()));
        }

        return InternalArgument.success(result);
    }

    @Override
    public @NotNull String toString() {
        return "ResolverArgument{" +
                "resolver=" + this.resolver +
                ", super=" + super.toString() + "}";
    }
}