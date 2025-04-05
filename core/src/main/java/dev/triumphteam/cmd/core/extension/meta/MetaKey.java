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
package dev.triumphteam.cmd.core.extension.meta;

import dev.triumphteam.cmd.core.extension.StringKey;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * Identifier for a specific meta value.
 *
 * @param <V> the type of the meta value.
 */
public final class MetaKey<V> extends StringKey {

    public static final MetaKey<String> NAME = new MetaKey<>("command.name", String.class);
    public static final MetaKey<String> DESCRIPTION = new MetaKey<>("command.description", String.class);
    public static final MetaKey<String> SYNTAX = new MetaKey<>("command.syntax", String.class);

    private final Class<V> valueType;

    private MetaKey(
            final @NotNull String key,
            final @NotNull Class<V> valueType
    ) {
        super(key);

        this.valueType = valueType;
    }

    /**
     * Factory method for creating a {@link MetaKey}.
     *
     * @param key the value of the key, normally separated by <code>.</code>.
     * @return a new {@link MetaKey}.
     */
    @Contract("_, _ -> new")
    public static <V> @NotNull MetaKey<V> of(final @NotNull String key, final @NotNull Class<V> valueType) {
        return new MetaKey<>(key, valueType);
    }

    @Override
    public @NotNull String toString() {
        return "MetaKey{super=" + super.toString() + "}";
    }
}