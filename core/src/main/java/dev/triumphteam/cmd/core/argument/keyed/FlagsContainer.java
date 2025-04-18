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

import org.jetbrains.annotations.NotNull;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@SuppressWarnings("unchecked")
abstract class FlagsContainer implements Arguments {

    private final Map<String, ArgumentValue> flags;

    public FlagsContainer(@NotNull final Map<String, ArgumentValue> flags) {
        this.flags = flags;
    }

    @Override
    public boolean hasFlag(@NotNull final String flag) {
        return this.flags.containsKey(flag);
    }

    @Override
    public @NotNull <T> Optional<T> getFlagValue(@NotNull final String flag, @NotNull final Class<T> type) {
        final ArgumentValue flagValue = this.flags.get(flag);

        if (flagValue == null) return Optional.empty();

        if (!(flagValue instanceof SimpleArgumentValue argFlagValue)) return Optional.empty();

        return Optional.ofNullable((T) argFlagValue.getValue());
    }

    @Override
    public @NotNull Optional<String> getFlagValue(@NotNull final String flag) {
        final ArgumentValue flagValue = this.flags.get(flag);

        if (flagValue == null) return Optional.empty();

        if (!(flagValue instanceof SimpleArgumentValue argFlagValue)) return Optional.empty();

        return Optional.of(argFlagValue.getAsString());
    }

    @Override
    public @NotNull Set<String> getAllFlags() {
        return this.flags.keySet();
    }

    @Override
    public boolean hasFlags() {
        return !this.flags.isEmpty();
    }

    @Override
    public String toString() {
        return "FlagsContainer{" +
                "flags=" + this.flags +
                '}';
    }
}