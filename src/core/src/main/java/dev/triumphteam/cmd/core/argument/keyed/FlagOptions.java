package dev.triumphteam.cmd.core.argument.keyed;

import dev.triumphteam.cmd.core.exceptions.CommandExecutionException;
import dev.triumphteam.cmd.core.exceptions.CommandRegistrationException;
import dev.triumphteam.cmd.core.suggestion.SuggestionKey;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
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

    FlagOptions(final Flag.@NonNull Builder builder) {

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
    public @NonNull String getKey() {
        // Will never happen.
        if (this.flag == null && this.longFlag == null) {
            throw new CommandExecutionException("Both flag and long flag can't be null.");
        }

        return (this.flag == null) ? this.longFlag : this.flag;
    }

    @Override
    public @NonNull String getDescription() {
        return this.description;
    }

    @Override
    public @Nullable SuggestionKey getSuggestion() {
        return this.suggestionKey;
    }

    @Override
    public boolean hasArgument() {
        return this.argument != null && this.argument != void.class;
    }

    @Override
    public @NonNull Class<?> getArgument() {
        return this.argument;
    }

    @Override
    public boolean equals(@Nullable final Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        final FlagOptions that = (FlagOptions) object;
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