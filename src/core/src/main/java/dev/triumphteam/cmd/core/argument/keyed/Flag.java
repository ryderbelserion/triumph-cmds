package dev.triumphteam.cmd.core.argument.keyed;

import dev.triumphteam.cmd.core.suggestion.EmptySuggestion;
import dev.triumphteam.cmd.core.suggestion.SuggestionKey;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public interface Flag {

    /**
     * Creates a {@link Flag} builder.
     *
     * @param flag The flag value to start with.
     * @return A {@link Argument.Builder} to create a new {@link Flag}.
     */
    static @NonNull Builder flag(@NonNull final String flag) {
        return new Builder().flag(flag);
    }

    /**
     * Creates a {@link Flag} builder.
     *
     * @param longFlag The long flag value to start with.
     * @return A {@link Argument.Builder} to create a new {@link Flag}.
     */
    static @NonNull Builder longFlag(@NonNull final String longFlag) {
        return new Builder().longFlag(longFlag);
    }

    /**
     * @return The flag identifier.
     */
    @Nullable String getFlag();

    /**
     * @return The long flag identifier.
     */
    @Nullable String getLongFlag();

    /**
     * @return Either be the {@link Flag#getFlag()} or the {@link Flag#getLongFlag()}..
     */
    @NonNull String getKey();

    /**
     * @return The description of the flag.
     */
    @NonNull String getDescription();

    /**
     * @return The {@link SuggestionKey} to be used.
     */
    @Nullable SuggestionKey getSuggestion();

    /**
     * @return Whether the flag contains arguments.
     */
    boolean hasArgument();

    /**
     * @return Gets the argument if there is one.
     */
    @NonNull Class<?> getArgument();

    /**
     * Simple builder for creating new {@link Flag}s.
     */
    final class Builder {

        private String flag;
        private String longFlag;
        private String description;
        private Class<?> argument = void.class;
        private SuggestionKey suggestionKey;

        /**
         * Sets the flag name.
         *
         * @param flag The flag to be used.
         * @return This builder.
         */
        public @NonNull Builder flag(@NonNull final String flag) {
            this.flag = flag;

            return this;
        }

        /**
         * Sets the long flag name.
         *
         * @param longFlag The long flag to be used.
         * @return This builder.
         */
        public @NonNull Builder longFlag(@NonNull final String longFlag) {
            this.longFlag = longFlag;

            return this;
        }

        /**
         * Sets the description of the Flag.
         *
         * @param description The description of the Flag.
         * @return This builder.
         */
        public @NonNull Builder description(@NonNull final String description) {
            this.description = description;

            return this;
        }

        /**
         * Sets the argument type of the Flag.
         *
         * @param argumentType The argument type of the Flag.
         * @return This builder.
         */
        public @NonNull Builder argument(@NonNull final Class<?> argumentType) {
            this.argument = argumentType;

            return this;
        }

        /**
         * Sets the suggestion key to be used by the Flag.
         * If not are supplied the Flag will use {@link EmptySuggestion} instead.
         *
         * @param suggestionKey The registered suggestion key.
         * @return This builder.
         */
        public @NonNull Builder suggestion(@NonNull final SuggestionKey suggestionKey) {
            this.suggestionKey = suggestionKey;

            return this;
        }

        /**
         * Builds the flag.
         *
         * @return A new {@link Flag} with the data from this builder.
         */
        public @NonNull Flag build() {
            return new FlagOptions(this);
        }

        @NonNull Class<?> getArgument() {
            return this.argument;
        }

        @NonNull String getFlag() {
            return this.flag;
        }

        @NonNull String getLongFlag() {
            return this.longFlag;
        }

        @NonNull String getDescription() {
            return this.description;
        }

        @Nullable SuggestionKey getSuggestionKey() {
            return this.suggestionKey;
        }
    }
}