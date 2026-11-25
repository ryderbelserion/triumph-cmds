package dev.triumphteam.cmd.core.argument.keyed;

import dev.triumphteam.cmd.core.suggestion.SuggestionKey;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import java.util.Objects;

final class SimpleArgument implements Argument {

    private final String name;
    private final String longName;
    private final Class<?> type;
    private final String description;
    private final SuggestionKey suggestionKey;
    private final boolean isLongNameArgument;

    public SimpleArgument(final Argument.@NonNull AbstractBuilder<?> argumentBuilder) {
        this.type = argumentBuilder.getType();
        this.name = argumentBuilder.getName();
        this.longName = argumentBuilder.getLongName();
        this.description = argumentBuilder.getDescription();
        this.suggestionKey = argumentBuilder.getSuggestionKey();
        this.isLongNameArgument = false;
    }

    public SimpleArgument(
            final String name,
            final String longName,
            final Class<?> type,
            final String description,
            final SuggestionKey suggestionKey,
            final boolean isLongNameArgument
    ) {
        this.name = name;
        this.longName = longName;
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
        return new SimpleArgument(this.name, this.longName, this.type, this.description, this.suggestionKey, true);
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

    @Override
    public boolean equals(final Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        final SimpleArgument that = (SimpleArgument) object;
        return Objects.equals(this.name, that.name) && Objects.equals(this.longName, that.longName) && Objects.equals(this.type, that.type) &&
                Objects.equals(this.description, that.description) && Objects.equals(this.suggestionKey, that.suggestionKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.name, this.longName, this.type, this.description, this.suggestionKey);
    }

    @Override
    public String toString() {
        return "SimpleArgument{" +
                "suggestionKey=" + this.suggestionKey +
                ", description='" + this.description + '\'' +
                ", type=" + this.type +
                ", longName='" + this.longName + '\'' +
                ", name='" + this.name + '\'' +
                '}';
    }
}
