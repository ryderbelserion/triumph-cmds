package dev.triumphteam.cmd.core.extension;

import dev.triumphteam.cmd.core.exceptions.CommandRegistrationException;
import dev.triumphteam.cmd.core.extension.annotation.AnnotationProcessor;
import dev.triumphteam.cmd.core.extension.argument.ArgumentValidator;
import dev.triumphteam.cmd.core.extension.command.CommandExecutor;
import dev.triumphteam.cmd.core.extension.command.Processor;
import dev.triumphteam.cmd.core.extension.sender.SenderExtension;
import org.jspecify.annotations.NonNull;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ExtensionBuilder<D, S, ST> {

    private final Map<Class<? extends Annotation>, AnnotationProcessor<? extends Annotation>> annotationProcessors = new HashMap<>();
    private final List<Processor<D, S>> processors = new ArrayList<>();

    private SenderExtension<D, S> senderExtension = null;
    private ArgumentValidator<S, ST> argumentValidator = null;
    private CommandExecutor<S> commandExecutor = null;
    private SuggestionMapper<ST> suggestionMapper = null;

    public <A extends Annotation> @NonNull ExtensionBuilder<D, S, ST> addAnnotationProcessor(
            final Class<A> annotation,
            @NonNull final AnnotationProcessor<A> annotationProcessor
    ) {
        this.annotationProcessors.put(annotation, annotationProcessor);

        return this;
    }

    public @NonNull ExtensionBuilder<D, S, ST> addProcessor(@NonNull final Processor<D, S> processor) {
        this.processors.add(processor);

        return this;
    }

    public @NonNull ExtensionBuilder<D, S, ST> setArgumentValidator(@NonNull final ArgumentValidator<S, ST> argumentValidator) {
        this.argumentValidator = argumentValidator;

        return this;
    }

    public @NonNull ExtensionBuilder<D, S, ST> setCommandExecutor(@NonNull final CommandExecutor<S> commandExecutor) {
        this.commandExecutor = commandExecutor;

        return this;
    }

    public @NonNull ExtensionBuilder<D, S, ST> setSenderExtension(@NonNull final SenderExtension<D, S> senderExtension) {
        this.senderExtension = senderExtension;

        return this;
    }

    public @NonNull ExtensionBuilder<D, S, ST> setSuggestionMapper(@NonNull final SuggestionMapper<ST> suggestionMapper) {
        this.suggestionMapper = suggestionMapper;

        return this;
    }

    public @NonNull CommandExtensions<D, S, ST> build(@NonNull final SenderExtension<D, S> defaultExtension) {
        if (this.argumentValidator == null) {
            throw new CommandRegistrationException("No argument validator was added to Command Manager.");
        }

        if (this.commandExecutor == null) {
            throw new CommandRegistrationException("No command executor was added to Command Manager.");
        }

        if (this.suggestionMapper == null) {
            throw new CommandRegistrationException("No suggestion mapper was added to Command Manager.");
        }

        return new CommandExtensions<>(
                this.senderExtension == null ? defaultExtension : this.senderExtension,
                this.annotationProcessors,
                this.processors,
                this.argumentValidator,
                this.commandExecutor,
                this.suggestionMapper
        );
    }
}