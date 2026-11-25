package dev.triumphteam.cmd.core.suggestion;

import dev.triumphteam.cmd.core.extension.SuggestionMapper;
import org.jspecify.annotations.NonNull;
import java.util.List;
import java.util.Map;

public final class SimpleSuggestion<S, ST> implements InternalSuggestion.Simple<S, ST> {

    private final SimpleSuggestionHolder<S, ST> holder;
    private final SuggestionMapper<ST> mapper;
    private final SuggestionMethod method;
    private final String extra;

    public SimpleSuggestion(
            @NonNull final SimpleSuggestionHolder<S, ST> holder,
            @NonNull final SuggestionMapper<ST> mapper,
            @NonNull final SuggestionMethod method
    ) {
        this(holder, mapper, method, "");
    }

    public SimpleSuggestion(
            @NonNull final SimpleSuggestionHolder<S, ST> holder,
            @NonNull final SuggestionMapper<ST> mapper,
            @NonNull final SuggestionMethod method,
            @NonNull final String extra
    ) {
        this.holder = holder;
        this.mapper = mapper;
        this.method = method;
        this.extra = extra;
    }

    @Override
    public @NonNull List<ST> getSuggestions(
            @NonNull final S sender,
            @NonNull final String current,
            @NonNull final List<String> arguments,
            @NonNull final Map<String, String> argumentsMap
    ) {
        return this.mapper.filter(current, this.holder.getSuggestions(SuggestionContext.of(current, sender, arguments, argumentsMap, this.extra)), this.method);
    }

    @Override
    public @NonNull InternalSuggestion<S, ST> copy(@NonNull final SuggestionMethod method, @NonNull final String extra) {
        return new SimpleSuggestion<>(this.holder, this.mapper, method, extra);
    }
}