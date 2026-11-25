package dev.triumphteam.cmd.core.argument.keyed;

import dev.triumphteam.cmd.core.argument.InternalArgument;
import dev.triumphteam.cmd.core.exceptions.CommandRegistrationException;
import dev.triumphteam.cmd.core.suggestion.EmptySuggestion;
import dev.triumphteam.cmd.core.suggestion.SuggestionKey;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import java.util.List;
import java.util.Set;

/**
 * An argument or "named" argument, is separate from the {@link InternalArgument}, as they are used within one.
 * This is used for explicit name to value arguments, for example <code>name:John</code>.
 */
public interface Argument {

    /**
     * Creates a builder for a {@link String} argument.
     *
     * @return A {@link Builder} for a {@link String} argument.
     */
    static Argument.@NonNull Builder forString() {
        return new Builder(String.class);
    }

    /**
     * Creates a builder for a {@link Integer} argument.
     *
     * @return A {@link Builder} for a {@link Integer} argument.
     */
    static Argument.@NonNull Builder forInt() {
        return new Builder(int.class);
    }

    /**
     * Creates a builder for a {@link Float} argument.
     *
     * @return A {@link Builder} for a {@link Float} argument.
     */
    static Argument.@NonNull Builder forFloat() {
        return new Builder(float.class);
    }

    /**
     * Creates a builder for a {@link Double} argument.
     *
     * @return A {@link Builder} for a {@link Double} argument.
     */
    static Argument.@NonNull Builder forDouble() {
        return new Builder(double.class);
    }

    /**
     * Creates a builder for a {@link Boolean} argument.
     *
     * @return A {@link Builder} for a {@link Boolean} argument.
     */
    static Argument.@NonNull Builder forBoolean() {
        return new Builder(boolean.class);
    }

    /**
     * Creates a builder for a custom type argument.
     *
     * @return A {@link Builder} for a custom type argument.
     */
    static Argument.@NonNull Builder forType(@NonNull final Class<?> type) {
        return new Builder(type);
    }

    /**
     * Creates a {@link List} builder of a custom type argument.
     *
     * @param type The type of the argument.
     * @return A {@link CollectionBuilder} for a custom type argument.
     */
    static Argument.@NonNull CollectionBuilder listOf(@NonNull final Class<?> type) {
        return new CollectionBuilder(List.class, type);
    }

    /**
     * Creates a {@link Set} builder of a custom type argument.
     *
     * @param type The type of the argument.
     * @return A {@link CollectionBuilder} for a custom type argument.
     */
    static Argument.@NonNull CollectionBuilder setOf(@NonNull final Class<?> type) {
        return new CollectionBuilder(Set.class, type);
    }

    /**
     * @return The type of the argument.
     */
    @NonNull Class<?> getType();

    /**
     * @return The name of the argument.
     */
    @NonNull String getName();

    /**
     * @return The long name of the argument, or null if it is not defined.
     */
    @Nullable String getLongName();

    /**
     * @return The description of the argument.
     */
    @NonNull String getDescription();

    /**
     * @return The {@link SuggestionKey} to be used.
     */
    @Nullable SuggestionKey getSuggestion();

    /**
     * @return a copy of the {@link Argument} marked as a long name argument, used for suggestions.
     */
    @NonNull Argument asLongNameArgument();

    /**
     * @return true if the argument is a long name argument, false otherwise.
     */
    boolean isLongNameArgument();

    /**
     * The default builder, simply extends the functionality of {@link AbstractBuilder}.
     */
    class Builder extends AbstractBuilder<Builder> {
        public Builder(@NonNull final Class<?> type) {
            super(type);
        }

        @Override
        protected @NonNull Builder getThis() {
            return this;
        }
    }

    /**
     * A builder for specific collections.
     * Adds the collection type and the separator to be used during execution.
     */
    final class CollectionBuilder extends AbstractBuilder<CollectionBuilder> {

        private final Class<?> collectionType;
        private String separator = ",";

        public CollectionBuilder(@NonNull final Class<?> collectionType, @NonNull final Class<?> type) {
            super(type);

            this.collectionType = collectionType;
        }

        @Override
        protected @NonNull CollectionBuilder getThis() {
            return this;
        }

        /**
         * Set the separator to be used to split an argument into a collection.
         *
         * @param separator The char, or string to use as a separator, supports regex.
         * @return This builder.
         */
        public Argument.@NonNull CollectionBuilder separator(@NonNull final String separator) {
            this.separator = separator;

            return this;
        }

        /**
         * Builds the argument.
         *
         * @return A new {@link Argument} with the data from this builder.
         */
        public @NonNull Argument build() {
            return new ListArgument(this);
        }

        @NonNull Class<?> getCollectionType() {
            return this.collectionType;
        }

        @NonNull String getSeparator() {
            return this.separator;
        }
    }

    abstract class AbstractBuilder<T extends AbstractBuilder<T>> {

        private final Class<?> type;

        private String name;
        private String longName = null;
        private String description;
        private SuggestionKey suggestionKey;

        private AbstractBuilder(@NonNull final Class<?> type) {
            this.type = type;
        }

        protected abstract @NonNull T getThis();

        /**
         * Sets the name of the argument.
         *
         * @param name The name of the argument.
         * @return This builder.
         */
        public @NonNull T name(@NonNull final String name) {
            this.name = name;

            return getThis();
        }

        /**
         * Sets the long name of the argument.
         *
         * @param longName The long name of the argument.
         * @return This builder.
         */
        public @NonNull T longName(@NonNull final String longName) {
            this.longName = longName;

            return getThis();
        }

        /**
         * Sets the description of the argument.
         *
         * @param description The description of the argument.
         * @return This builder.
         */
        public @NonNull T description(@NonNull final String description) {
            this.description = description;

            return getThis();
        }

        /**
         * Sets the suggestion key to be used by the argument.
         * If not are supplied the argument will use {@link EmptySuggestion} instead.
         *
         * @param suggestionKey The registered suggestion key.
         * @return This builder.
         */
        public @NonNull T suggestion(@NonNull final SuggestionKey suggestionKey) {
            this.suggestionKey = suggestionKey;

            return getThis();
        }

        /**
         * Builds the argument.
         *
         * @return A new {@link Argument} with the data from this builder.
         */
        public @NonNull Argument build() {
            return new SimpleArgument(this);
        }

        @NonNull Class<?> getType() {
            return this.type;
        }

        @NonNull String getName() {
            if (this.name == null || this.name.isEmpty()) {
                throw new CommandRegistrationException("Argument is missing a name!");
            }

            return this.name;
        }

        @Nullable String getLongName() {
            return this.longName;
        }

        @NonNull String getDescription() {
            return this.description;
        }

        @Nullable SuggestionKey getSuggestionKey() {
            return this.suggestionKey;
        }
    }
}