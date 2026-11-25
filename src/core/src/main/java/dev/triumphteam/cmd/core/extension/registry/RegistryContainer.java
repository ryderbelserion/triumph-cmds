package dev.triumphteam.cmd.core.extension.registry;

import dev.triumphteam.cmd.core.suggestion.SuggestionRegistry;
import org.jspecify.annotations.NonNull;

public class RegistryContainer<D, S, ST> {

    private final ArgumentRegistry<S, ST> argumentRegistry = new ArgumentRegistry<>();
    private final NamedArgumentRegistry namedArgumentRegistry = new NamedArgumentRegistry();
    private final FlagRegistry flagRegistry = new FlagRegistry();
    private final RequirementRegistry<D, S> requirementRegistry = new RequirementRegistry<>();
    private final MessageRegistry<S> messageRegistry = new MessageRegistry<>();
    private final SuggestionRegistry<S, ST> suggestionRegistry = new SuggestionRegistry<>();

    public @NonNull ArgumentRegistry<S, ST> getArgumentRegistry() {
        return this.argumentRegistry;
    }

    public @NonNull NamedArgumentRegistry getNamedArgumentRegistry() {
        return this.namedArgumentRegistry;
    }

    public @NonNull FlagRegistry getFlagRegistry() {
        return this.flagRegistry;
    }

    public @NonNull RequirementRegistry<D, S> getRequirementRegistry() {
        return this.requirementRegistry;
    }

    public @NonNull MessageRegistry<S> getMessageRegistry() {
        return this.messageRegistry;
    }

    public @NonNull SuggestionRegistry<S, ST> getSuggestionRegistry() {
        return this.suggestionRegistry;
    }
}