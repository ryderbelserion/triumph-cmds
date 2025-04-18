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

import dev.triumphteam.cmd.core.suggestion.EmptySuggestion;
import dev.triumphteam.cmd.core.suggestion.SuggestionKey;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Flag {

    /**
     * Creates a {@link Flag} builder.
     *
     * @param flag the flag value to start with.
     * @return a {@link Argument.Builder} to create a new {@link Flag}.
     */
    @Contract("_ -> new")
    static @NotNull Builder flag(@NotNull final String flag) {
        return new Builder().flag(flag);
    }

    /**
     * Creates a {@link Flag} builder.
     *
     * @param longFlag the long flag value to start with.
     * @return a {@link Argument.Builder} to create a new {@link Flag}.
     */
    @Contract("_ -> new")
    static @NotNull Builder longFlag(@NotNull final String longFlag) {
        return new Builder().longFlag(longFlag);
    }

    /**
     * @return the flag identifier.
     */
    @Nullable String getFlag();

    /**
     * @return the long flag identifier.
     */
    @Nullable String getLongFlag();

    /**
     * @return either be the {@link Flag#getFlag()} or the {@link Flag#getLongFlag()}..
     */
    @NotNull String getKey();

    /**
     * @return the description of the flag.
     */
    @NotNull String getDescription();

    /**
     * @return the {@link SuggestionKey} to be used.
     */
    @Nullable SuggestionKey getSuggestion();

    /**
     * @return whether the flag contains arguments.
     */
    boolean hasArgument();

    /**
     * @return gets the argument if there is one.
     */
    @Nullable Class<?> getArgument();

    /**
     * Simple builder for creating new {@link Flag}s.
     */
    final class Builder {

        private String flag;
        private String longFlag;
        private String description;
        private Class<?> argument;
        private SuggestionKey suggestionKey;

        /**
         * Sets the flag name.
         *
         * @param flag the flag to be used.
         * @return this builder.
         */
        @Contract("_ -> this")
        public @NotNull Builder flag(@NotNull final String flag) {
            this.flag = flag;
            return this;
        }

        /**
         * Sets the long flag name.
         *
         * @param longFlag the long flag to be used.
         * @return this builder.
         */
        @Contract("_ -> this")
        public @NotNull Builder longFlag(@NotNull final String longFlag) {
            this.longFlag = longFlag;
            return this;
        }

        /**
         * Sets the description of the Flag.
         *
         * @param description the description of the Flag.
         * @return this builder.
         */
        @Contract("_ -> this")
        public @NotNull Builder description(@NotNull final String description) {
            this.description = description;
            return this;
        }

        /**
         * Sets the argument type of the Flag.
         *
         * @param argumentType the argument type of the Flag.
         * @return this builder.
         */
        @Contract("_ -> this")
        public @NotNull Builder argument(@NotNull final Class<?> argumentType) {
            this.argument = argumentType;
            return this;
        }

        /**
         * Sets the suggestion key to be used by the Flag.
         * If not are supplied the Flag will use {@link EmptySuggestion} instead.
         *
         * @param suggestionKey the registered suggestion key.
         * @return this builder.
         */
        @Contract("_ -> this")
        public @NotNull Builder suggestion(@NotNull final SuggestionKey suggestionKey) {
            this.suggestionKey = suggestionKey;
            return this;
        }

        /**
         * Builds the flag.
         *
         * @return a new {@link Flag} with the data from this builder.
         */
        @Contract(" -> new")
        public @NotNull Flag build() {
            return new FlagOptions(this);
        }

        @NotNull Class<?> getArgument() {
            return this.argument;
        }

        @NotNull String getFlag() {
            return this.flag;
        }

        @NotNull String getLongFlag() {
            return this.longFlag;
        }

        @NotNull String getDescription() {
            return this.description;
        }

        @Nullable SuggestionKey getSuggestionKey() {
            return this.suggestionKey;
        }
    }
}