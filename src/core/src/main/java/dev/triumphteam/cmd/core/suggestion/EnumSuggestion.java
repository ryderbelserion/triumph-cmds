package dev.triumphteam.cmd.core.suggestion;

import dev.triumphteam.cmd.core.extension.SuggestionMapper;
import dev.triumphteam.cmd.core.util.EnumUtils;
import org.jspecify.annotations.NonNull;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public final class EnumSuggestion<S, ST> implements InternalSuggestion.Simple<S, ST> {

    private final Class<? extends Enum<?>> enumType;
    private final SuggestionMapper<ST> mapper;
    private final SuggestionMethod method;
    private final boolean suggestLowercase;

    public EnumSuggestion(
            @NonNull final Class<? extends Enum<?>> enumType,
            @NonNull final SuggestionMapper<ST> mapper,
            @NonNull final SuggestionMethod method,
            final boolean suggestLowercase
    ) {
        this.enumType = enumType;
        this.mapper = mapper;
        this.method = method;
        this.suggestLowercase = suggestLowercase;

        EnumUtils.populateCache(enumType);
    }

    @Override
    public @NonNull List<ST> getSuggestions(
            @NonNull final S sender,
            @NonNull final String current,
            @NonNull final List<String> arguments,
            @NonNull final Map<String, String> argumentsMap
    ) {
        final List<String> suggestions = EnumUtils.getEnumConstants(enumType)
                .values()
                .stream()
                .map(it -> {
                    final Enum<?> constant = it.get();

                    if (constant == null) return null;

                    final String name = constant.name();

                    return this.suggestLowercase ? name.toLowerCase() : name;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return this.mapper.filter(current, this.mapper.map(suggestions), this.method);
    }

    @Override
    public boolean equals(final Object object) {
        if (object == null || getClass() != object.getClass()) return false;

        final EnumSuggestion<?, ?> that = (EnumSuggestion<?, ?>) object;

        return this.suggestLowercase == that.suggestLowercase && Objects.equals(this.enumType, that.enumType) && Objects.equals(this.mapper, that.mapper);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.enumType, this.suggestLowercase, this.mapper);
    }

    @Override
    public @NonNull String toString() {
        return "EnumSuggestion{" +
                "enumType=" + this.enumType +
                ", suggestLowercase=" + this.suggestLowercase +
                ", mapper=" + this.mapper +
                '}';
    }
}