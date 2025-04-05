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
import org.jetbrains.annotations.Nullable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Basically a holder that contains all the needed arguments for the command.
 */
final class NamedGroup implements ArgumentGroup<Argument> {

    private final Map<String, Argument> arguments = new HashMap<>();

    NamedGroup(@NotNull final List<Argument> arguments) {
        arguments.forEach(this::addArgument);
    }

    public void addArgument(@NotNull final Argument argument) {
        this.arguments.put(argument.getName(), argument);
    }

    @Override
    public @NotNull Set<String> getAllNames() {
        return this.arguments.keySet();
    }

    @Override
    public boolean isEmpty() {
        return this.arguments.isEmpty();
    }

    @Override
    public @Nullable Argument matchExact(@NotNull final String token) {
        return this.arguments.get(token);
    }

    @Override
    public @Nullable Argument matchPartialSingle(@NotNull final String token) {
        final List<Argument> arguments = this.arguments.entrySet()
                .stream()
                .filter(it -> it.getKey().startsWith(token))
                .map(Map.Entry::getValue)
                .toList();

        if (arguments.size() != 1) return null;

        return arguments.getFirst();
    }

    @Override
    public @NotNull Set<Argument> getAll() {
        return new HashSet<>(this.arguments.values());
    }
}