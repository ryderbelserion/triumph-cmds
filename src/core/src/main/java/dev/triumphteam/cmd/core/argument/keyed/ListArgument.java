package dev.triumphteam.cmd.core.argument.keyed;

import dev.triumphteam.cmd.core.suggestion.SuggestionKey;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import java.util.Objects;

public final class ListArgument implements Argument {

    private final String name;
    private final String longName;
    private final Class<?> collectionType;
    private final String separator;
    private final Class<?> type;
    private final String description;
    private final SuggestionKey suggestionKey;
    private final boolean isLongNameArgument;

    public ListArgument(final Argument.@NonNull CollectionBuilder argumentBuilder) {
        this.type = argumentBuilder.getType();
        this.name = argumentBuilder.getName();
        this.longName = argumentBuilder.getLongName();
        this.description = argumentBuilder.getDescription();
        this.suggestionKey = argumentBuilder.getSuggestionKey();
        this.collectionType = argumentBuilder.getCollectionType();
        this.separator = argumentBuilder.getSeparator();
        this.isLongNameArgument = false;
    }

    public ListArgument(
            final String name,
            final String longName,
            final Class<?> collectionType,
            final String separator,
            final Class<?> type,
            final String description,
            final SuggestionKey suggestionKey,
            final boolean isLongNameArgument
    ) {
        this.name = name;
        this.longName = longName;
        this.collectionType = collectionType;
        this.separator = separator;
        this.type = type;
        this.description = description;
        this.suggestionKey = suggestionKey;
        this.isLongNameArgument = isLongNameArgument;
    }

    @Override
    public @NonNull Class<?> getType() {
        return this.type;
    }

    @Override
    public @NonNull String getName() {
        return this.name;
    }

    @Override
    public @Nullable String getLongName() {
        return this.longName;
    }

    @Override
    public @NonNull Argument asLongNameArgument() {
        return new ListArgument(this.name, this.longName, this.collectionType, this.separator, this.type, this.description, this.suggestionKey, true);
    }

    @Override
    public boolean isLongNameArgument() {
        return this.isLongNameArgument;
    }

    @Override
    public @NonNull String getDescription() {
        return this.description;
    }

    @Override
    public @Nullable SuggestionKey getSuggestion() {
        return this.suggestionKey;
    }

    public @NonNull Class<?> getCollectionType() {
        return this.collectionType;
    }

    public @NonNull String getSeparator() {
        return this.separator;
    }

    @Override
    public boolean equals(final Object object) {
        if (object == null || getClass() != object.getClass()) return false;

        final ListArgument that = (ListArgument) object;

        return Objects.equals(this.name, that.name) && Objects.equals(this.longName, that.longName) && Objects.equals(this.collectionType, that.collectionType) && Objects.equals(this.separator, that.separator) &&
                Objects.equals(this.type, that.type) && Objects.equals(this.description, that.description) && Objects.equals(this.suggestionKey, that.suggestionKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.name, this.longName, this.collectionType, this.separator, this.type, this.description, this.suggestionKey);
    }

    @Override
    public String toString() {
        return "ListArgument{" +
                "suggestionKey=" + this.suggestionKey +
                ", description='" + this.description + '\'' +
                ", type=" + this.type +
                ", separator='" + this.separator + '\'' +
                ", collectionType=" + this.collectionType +
                ", longName='" + this.longName + '\'' +
                ", name='" + this.name + '\'' +
                '}';
    }
}