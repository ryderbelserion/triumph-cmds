package dev.triumphteam.cmd.core.suggestion;

import dev.triumphteam.cmd.core.extension.SuggestionMapper;
import dev.triumphteam.cmd.core.extension.registry.Registry;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Registry used for registering new suggestions for all commands to use.
 *
 * @param <S> The sender type.
 */
public final class SuggestionRegistry<S, ST> implements Registry {

    private final Map<SuggestionKey, InternalSuggestion<S, ST>> suggestions = new HashMap<>();
    private final Map<Class<?>, InternalSuggestion<S, ST>> typeSuggestions = new HashMap<>();

    public void register(
            @NonNull final SuggestionKey key,
            @NonNull final InternalSuggestion<S, ST> suggestion
    ) {
        this.suggestions.put(key, suggestion);
    }

    public void register(
            @NonNull final SuggestionKey key,
            final SuggestionResolver.@NonNull Simple<S> resolver,
            @NonNull final SuggestionMethod method,
            @NonNull final SuggestionMapper<ST> suggestionMapper
    ) {
        this.suggestions.put(key, new SimpleSuggestion<>(new SimpleSuggestionHolder.SimpleResolver<>(resolver, suggestionMapper), suggestionMapper, method));
    }

    public void registerRich(
            @NonNull final SuggestionKey key,
            @NonNull final SuggestionResolver<S, ST> resolver,
            @NonNull final SuggestionMethod method,
            @NonNull final SuggestionMapper<ST> suggestionMapper
    ) {
        this.suggestions.put(key, new SimpleSuggestion<>(new SimpleSuggestionHolder.RichResolver<>(resolver), suggestionMapper, method));
    }

    public void registerStatic(
            @NonNull final SuggestionKey key,
            @NonNull final List<String> suggestions,
            @NonNull final SuggestionMethod method,
            @NonNull final SuggestionMapper<ST> suggestionMapper
    ) {
        this.suggestions.put(key, new StaticSuggestion<>(new SimpleSuggestionHolder.SimpleStatic<>(suggestions, suggestionMapper.map(suggestions)), suggestionMapper, method));
    }

    public void registerStaticRich(
            @NonNull final SuggestionKey key,
            @NonNull final List<ST> suggestions,
            @NonNull final SuggestionMethod method,
            @NonNull final SuggestionMapper<ST> suggestionMapper
    ) {
        this.suggestions.put(key, new StaticSuggestion<>(new SimpleSuggestionHolder.RichStatic<>(suggestions, suggestionMapper.mapBackwards(suggestions)), suggestionMapper, method));
    }

    public void register(
            @NonNull final Class<?> type,
            final SuggestionResolver.@NonNull Simple<S> resolver,
            @NonNull final SuggestionMethod method,
            @NonNull final SuggestionMapper<ST> suggestionMapper
    ) {
        this.typeSuggestions.put(type, new SimpleSuggestion<>(new SimpleSuggestionHolder.SimpleResolver<>(resolver, suggestionMapper), suggestionMapper, method));
    }

    public void registerRich(
            @NonNull final Class<?> type,
            @NonNull final SuggestionResolver<S, ST> resolver,
            @NonNull final SuggestionMethod method,
            @NonNull final SuggestionMapper<ST> suggestionMapper
    ) {
        this.typeSuggestions.put(type, new SimpleSuggestion<>(new SimpleSuggestionHolder.RichResolver<>(resolver), suggestionMapper, method));
    }

    public @Nullable InternalSuggestion<S, ST> getSuggestion(@Nullable final SuggestionKey key) {
        if (key == null) return null;

        return this.suggestions.get(key);
    }

    public @Nullable InternalSuggestion<S, ST> getSuggestion(@NonNull final Class<?> type) {
        return this.typeSuggestions.get(type);
    }
}