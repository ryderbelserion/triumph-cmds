package dev.triumphteam.cmd.core.extension.defaults;

import dev.triumphteam.cmd.core.extension.SuggestionMapper;
import dev.triumphteam.cmd.core.suggestion.SuggestionMethod;
import org.jspecify.annotations.NonNull;
import java.util.List;
import java.util.stream.Collectors;

public final class DefaultSuggestionMapper implements SuggestionMapper<String> {

    @Override
    public @NonNull List<String> map(@NonNull final List<String> values, @NonNull final Class<?> type) {
        return values;
    }

    @Override
    public @NonNull List<String> mapBackwards(@NonNull final List<String> values) {
        return values;
    }

    @Override
    public @NonNull List<String> filter(@NonNull final String input, @NonNull final List<String> values, final SuggestionMethod method) {
        return switch (method) {
            case STARTS_WITH -> values.stream().filter(it -> it.toLowerCase().startsWith(input.toLowerCase())).collect(Collectors.toList());
            case CONTAINS -> values.stream().filter(it -> it.toLowerCase().contains(input.toLowerCase())).collect(Collectors.toList());
            default -> values;
        };
    }

    @Override
    public @NonNull Class<?> getType() {
        return String.class;
    }
}