package dev.triumphteam.cmd.core.argument.keyed;

import dev.triumphteam.cmd.core.util.Pair;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public final class KeyedArguments extends FlagsContainer {

    private final Map<String, ArgumentValue> values;
    private final List<String> nonTokens;

    public KeyedArguments(
            @NonNull final Map<String, ArgumentValue> values,
            @NonNull final Map<String, ArgumentValue> flags,
            @NonNull final List<String> nonTokens
    ) {
        super(flags);

        this.values = values;
        this.nonTokens = nonTokens;
    }

    @Override
    public <T> @NonNull Optional<T> getArgument(@NonNull final String name, @NonNull final Class<T> type) {
        return Optional.ofNullable((T) getValue(name));
    }

    @Override
    public <T> @NonNull Optional<List<T>> getListArgument(@NonNull final String name, @NonNull final Class<T> type) {
        final List<T> value = (List<T>) getValue(name);

        return Optional.ofNullable(value);
    }

    @Override
    public <T> @NonNull Optional<Set<T>> getSetArgument(@NonNull final String name, @NonNull final Class<T> type) {
        final Set<T> value = (Set<T>) getValue(name);

        return Optional.ofNullable(value);
    }

    private @Nullable Object getValue(@NonNull final String name) {
        final ArgumentValue argumentValue = this.values.get(name);

        if (argumentValue == null) return null;

        if (argumentValue instanceof SimpleArgumentValue index) {
            return index.getValue();
        }

        return null;
    }

    @Override
    public @NonNull Map<String, Object> getAllArguments() {
        return this.values.entrySet().stream().map(entry -> {
            final ArgumentValue value = entry.getValue();

            if (value instanceof SimpleArgumentValue index) {
                return new Pair<>(entry.getKey(), index.getValue());
            }

            return new Pair<>(entry.getKey(), null);
        }).collect(Collectors.toMap(Pair::first, Pair::second));
    }

    @Override
    public @NonNull String getText() {
        return getText(" ");
    }

    @Override
    public @NonNull String getText(@NonNull final String delimiter) {
        return String.join(delimiter, this.nonTokens);
    }

    @Override
    public boolean hasArguments() {
        return !this.values.isEmpty();
    }

    @Override
    public @NonNull String toString() {
        return "Arguments{" +
                "values=" + this.values +
                ", super=" + super.toString() + "}";
    }
}