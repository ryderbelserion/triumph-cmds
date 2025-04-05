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

import dev.triumphteam.cmd.core.exceptions.CommandExecutionException;
import dev.triumphteam.cmd.core.exceptions.CommandRegistrationException;
import dev.triumphteam.cmd.core.suggestion.SuggestionKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.Objects;

/**
 * Contains all the "settings" for the flag.
 */
final class FlagOptions implements Flag {

    private final String flag;
    private final String longFlag;
    private final String description;
    private final Class<?> argument;
    private final SuggestionKey suggestionKey;

    FlagOptions(@NotNull final Flag.Builder builder) {

        final String flag = builder.getFlag();
        final String longFlag = builder.getLongFlag();

        this.flag = flag.isEmpty() ? null : flag;
        this.longFlag = longFlag.isEmpty() ? null : longFlag;

        this.description = builder.getDescription();
        this.argument = builder.getArgument();
        this.suggestionKey = builder.getSuggestionKey();

        if (this.flag == null && this.longFlag == null) {
            throw new CommandRegistrationException("Flag can't have both normal and long flag empty!");
        }

        FlagValidator.validate(flag);
        FlagValidator.validate(longFlag);
    }

    @Override
    public @Nullable String getFlag() {
        return this.flag;
    }

    @Override
    public @Nullable String getLongFlag() {
        return this.longFlag;
    }

    @Override
    public @NotNull String getKey() {
        // Will never happen.
        if (this.flag == null && this.longFlag == null) {
            throw new CommandExecutionException("Both flag and long flag can't be null.");
        }

        return (this.flag == null) ? this.longFlag : this.flag;
    }

    @Override
    public @NotNull String getDescription() {
        return this.description;
    }

    @Override
    public @Nullable SuggestionKey getSuggestion() {
        return this.suggestionKey;
    }

    @Override
    public boolean hasArgument() {
        return this.argument != null && this.argument != Void.TYPE;
    }

    @Override
    public @Nullable Class<?> getArgument() {
        return this.argument;
    }

    @Override
    public boolean equals(@Nullable final Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        final FlagOptions that = (FlagOptions) o;

        return Objects.equals(this.flag, that.flag) && Objects.equals(this.longFlag, that.longFlag) && Objects.equals(this.argument, that.argument);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.flag, this.longFlag, this.argument);
    }

    @Override
    public String toString() {
        return "FlagOptions{" +
                "flag='" + getKey() + '\'' +
                '}';
    }
}