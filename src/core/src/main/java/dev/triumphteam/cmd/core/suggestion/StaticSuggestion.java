package dev.triumphteam.cmd.core.suggestion;

import dev.triumphteam.cmd.core.extension.SuggestionMapper;
import org.jspecify.annotations.NonNull;
import java.util.List;
import java.util.Map;

public final class StaticSuggestion<S, ST> implements InternalSuggestion.Simple<S, ST> {

    private final SimpleSuggestionHolder.Static<ST> holder;
    private final SuggestionMapper<ST> mapper;
    private final SuggestionMethod method;

    public StaticSuggestion(
            SimpleSuggestionHolder.Static<ST> holder,
            @NonNull final SuggestionMapper<ST> mapper,
            @NonNull final SuggestionMethod method
    ) {
        this.holder = holder;
        this.mapper = mapper;
        this.method = method;
    }

    @Override
    public @NonNull List<ST> getSuggestions(
            @NonNull final S sender,
            @NonNull final String current,
            @NonNull final List<String> arguments,
            @NonNull final Map<String, String> argumentsMap
    ) {
        return this.mapper.filter(current, getSuggestions(), this.method);
    }

    public boolean contains(@NonNull final String suggestion) {
        return this.holder.contains(suggestion);
    }

    public @NonNull List<ST> getSuggestions() {
        return this.holder.getSuggestions();
    }
}