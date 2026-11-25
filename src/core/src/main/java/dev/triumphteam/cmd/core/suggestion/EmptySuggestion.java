package dev.triumphteam.cmd.core.suggestion;

import org.jspecify.annotations.NonNull;
import java.util.List;
import java.util.Map;
import static java.util.Collections.emptyList;

public final class EmptySuggestion<S, ST> implements InternalSuggestion.Simple<S, ST> {

    @Override
    public @NonNull List<ST> getSuggestions(
            @NonNull final S sender,
            @NonNull final String current,
            @NonNull final List<String> arguments,
            @NonNull final Map<String, String> argumentsMap
    ) {
        return emptyList();
    }

    @Override
    public @NonNull String toString() {
        return "EmptySuggestion{}";
    }
}