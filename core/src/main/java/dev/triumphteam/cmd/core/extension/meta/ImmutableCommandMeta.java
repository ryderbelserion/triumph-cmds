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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.Map;
import java.util.Optional;

@SuppressWarnings("unchecked")
final class ImmutableCommandMeta implements CommandMeta {

    private final CommandMeta parentMeta;
    private final Map<MetaKey<?>, Object> meta;

    public ImmutableCommandMeta(
            @Nullable final CommandMeta parentMeta,
            @NotNull final Map<MetaKey<?>, Object> meta
    ) {
        this.parentMeta = parentMeta;
        this.meta = meta;
    }

    @Override
    public @NotNull <V> Optional<V> get(@NotNull final MetaKey<V> metaKey) {
        return Optional.ofNullable(getNullable(metaKey));
    }

    @Override
    public <V> @Nullable V getNullable(@NotNull final MetaKey<V> metaKey) {
        return (V) this.meta.get(metaKey);
    }

    @Override
    public <V> V getOrDefault(@NotNull final MetaKey<V> metaKey, @Nullable final V def) {
        return (V) this.meta.getOrDefault(metaKey, def);
    }

    @Override
    public <V> boolean isPresent(@NotNull final MetaKey<V> metaKey) {
        return this.meta.containsKey(metaKey);
    }

    @Override
    public @Nullable CommandMeta getParentMeta() {
        return this.parentMeta;
    }

    @Override
    public String toString() {
        return "ImmutableCommandMeta{" +
                "meta=" + this.meta +
                '}';
    }
}