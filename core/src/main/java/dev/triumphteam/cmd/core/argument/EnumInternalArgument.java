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

import java.lang.ref.WeakReference;
import java.util.function.BiFunction;

import static dev.triumphteam.cmd.core.util.EnumUtils.getEnumConstants;
import static dev.triumphteam.cmd.core.util.EnumUtils.populateCache;

/**
 * An argument type for {@link Enum}s.
 * This is needed instead of the normal {@link ResolverInternalArgument} because of different types of enums, which requires the class.
 *
 * @param <S> the sender type.
 */
public final class EnumInternalArgument<S> extends StringInternalArgument<S> {

    private final Class<? extends Enum<?>> enumType;

    public EnumInternalArgument(
            @NotNull final CommandMeta meta,
            @NotNull final String name,
            @NotNull final String description,
            @NotNull final Class<? extends Enum<?>> type,
            @NotNull final Suggestion<S> suggestion,
            final boolean optional
    ) {
        super(meta, name, description, type, suggestion, optional);

        this.enumType = type;

        // Populates on creation to reduce runtime of first run for certain enums, like Bukkit's Material.
        populateCache(type);
    }

    /**
     * Resolves the argument type.
     *
     * @param sender the sender to resolve to.
     * @param value the {@link String} argument value.
     * @return an {@link Enum} value of the correct type.
     */
    @Override
    public @NotNull Result<Object, BiFunction<@NotNull CommandMeta, @NotNull String, @NotNull InvalidArgumentContext>> resolve(
            @NotNull final S sender,
            @NotNull final String value,
            @Nullable final Object provided
    ) {
        final WeakReference<? extends Enum<?>> reference = getEnumConstants(this.enumType).get(value.toUpperCase());

        if (reference == null) {
            return InternalArgument.invalid((meta, syntax) -> new InvalidArgumentContext(meta, syntax, value, getName(), getType()));
        }

        final Enum<?> enumValue = reference.get();

        if (enumValue == null) {
            return InternalArgument.invalid((commands, arguments) -> new InvalidArgumentContext(commands, arguments, value, getName(), getType()));
        }

        return InternalArgument.success(enumValue);
    }

    @Override
    public @NotNull String toString() {
        return "EnumArgument{" +
                "enumType=" + this.enumType +
                ", super=" + super.toString() + "}";
    }
}