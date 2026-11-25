package dev.triumphteam.cmd.core.extension;

import dev.triumphteam.cmd.core.suggestion.SuggestionMethod;
import org.jspecify.annotations.NonNull;
import java.util.List;

public interface SuggestionMapper<ST> {

    @NonNull List<ST> map(@NonNull final List<String> values, @NonNull final Class<?> type);

    default @NonNull List<ST> map(@NonNull final List<String> values) {
        return map(values, String.class);
    }

    @NonNull List<String> mapBackwards(@NonNull final List<ST> values);

    @NonNull List<ST> filter(@NonNull final String input, @NonNull final List<ST> values, final SuggestionMethod method);

    @NonNull Class<?> getType();
}