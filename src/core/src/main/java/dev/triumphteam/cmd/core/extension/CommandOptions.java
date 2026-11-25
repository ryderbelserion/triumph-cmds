package dev.triumphteam.cmd.core.extension;

import dev.triumphteam.cmd.core.CommandManager;
import dev.triumphteam.cmd.core.extension.sender.SenderExtension;
import dev.triumphteam.cmd.core.suggestion.SuggestionMethod;
import org.jspecify.annotations.NonNull;
import java.util.function.Consumer;

public class CommandOptions<O extends CommandOptions<O, M, D, S, ST>, M extends CommandManager<M, O, D, S, ST>, D, S, ST> {

    private final CommandExtensions<D, S, ST> commandExtensions;
    private final boolean suggestLowercaseEnum;
    private final Consumer<M> setup;
    private final SuggestionMethod suggestionMethod;

    public CommandOptions(
            @NonNull final SenderExtension<D, S> senderExtension,
            @NonNull final Builder<?, M, O, D, S, ST> builder
    ) {

        this.commandExtensions = builder.extensionBuilder.build(senderExtension);
        this.suggestLowercaseEnum = builder.suggestLowercaseEnum;
        this.setup = builder.setup;
        this.suggestionMethod = builder.suggestionMethod;
    }

    public @NonNull CommandExtensions<D, S, ST> getCommandExtensions() {
        return commandExtensions;
    }

    public @NonNull Consumer<M> getSetup() {
        return setup;
    }

    public @NonNull SuggestionMethod getDefaultSuggestionMethod() {
        return suggestionMethod;
    }

    public boolean suggestLowercaseEnum() {
        return suggestLowercaseEnum;
    }

    public static abstract class Builder<B extends Builder<B, M, O, D, S, ST>, M extends CommandManager<M, O, D, S, ST>, O extends CommandOptions<O, M, D, S, ST>, D, S, ST> {

        private final ExtensionBuilder<D, S, ST> extensionBuilder = new ExtensionBuilder<>();
        private Consumer<M> setup = setup -> {};
        private boolean suggestLowercaseEnum = false;
        private SuggestionMethod suggestionMethod = SuggestionMethod.STARTS_WITH;

        protected abstract @NonNull B getThis();

        public @NonNull B setup(@NonNull final Consumer<M> consumer) {
            this.setup = consumer;

            return getThis();
        }

        public @NonNull B extensions(@NonNull final Consumer<ExtensionBuilder<D, S, ST>> consumer) {
            consumer.accept(this.extensionBuilder);

            return getThis();
        }

        public @NonNull B suggestLowercaseEnum() {
            this.suggestLowercaseEnum = true;

            return getThis();
        }

        public @NonNull B defaultSuggestionMethod(@NonNull final SuggestionMethod suggestionMethod) {
            this.suggestionMethod = suggestionMethod;

            return getThis();
        }
    }
}