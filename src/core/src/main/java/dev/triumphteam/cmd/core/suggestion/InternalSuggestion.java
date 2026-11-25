package dev.triumphteam.cmd.core.suggestion;

import org.jspecify.annotations.NonNull;
import java.util.List;
import java.util.Map;

public interface InternalSuggestion<S, ST> {

    default @NonNull InternalSuggestion<S, ST> copy(@NonNull final SuggestionMethod method, @NonNull final String extra) {
        return this;
    }

    interface Simple<S, ST> extends InternalSuggestion<S, ST> {

        @NonNull List<ST> getSuggestions(
                @NonNull final S sender,
                @NonNull final String current,
                @NonNull final List<String> arguments,
                @NonNull final Map<String, String> argumentsMap
        );
    }
}