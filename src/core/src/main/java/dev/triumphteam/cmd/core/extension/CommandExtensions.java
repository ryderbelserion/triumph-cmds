package dev.triumphteam.cmd.core.extension;

import dev.triumphteam.cmd.core.extension.command.CommandExecutor;
import dev.triumphteam.cmd.core.extension.annotation.AnnotationProcessor;
import dev.triumphteam.cmd.core.extension.argument.ArgumentValidator;
import dev.triumphteam.cmd.core.extension.command.Processor;
import dev.triumphteam.cmd.core.extension.sender.SenderExtension;
import org.jspecify.annotations.NonNull;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;

public final class CommandExtensions<D, S, ST> {

    private final Map<Class<? extends Annotation>, AnnotationProcessor<? extends Annotation>> annotationProcessors;
    private final List<Processor<D, S>> processors;

    private final SenderExtension<D, S> senderExtension;
    private final ArgumentValidator<S, ST> argumentValidator;
    private final CommandExecutor<S> commandExecutor;
    private final SuggestionMapper<ST> suggestionMapper;

    public CommandExtensions(
            @NonNull final SenderExtension<D, S> senderExtension,
            @NonNull final Map<Class<? extends Annotation>, AnnotationProcessor<? extends Annotation>> annotationProcessors,
            @NonNull final List<Processor<D, S>> processors,
            @NonNull final ArgumentValidator<S, ST> argumentValidator,
            @NonNull final CommandExecutor<S> commandExecutor,
            @NonNull final SuggestionMapper<ST> suggestionMapper
    ) {
        this.senderExtension = senderExtension;
        this.annotationProcessors = annotationProcessors;
        this.processors = processors;
        this.argumentValidator = argumentValidator;
        this.commandExecutor = commandExecutor;
        this.suggestionMapper = suggestionMapper;
    }

    public @NonNull Map<Class<? extends Annotation>, AnnotationProcessor<? extends Annotation>> getAnnotationProcessors() {
        return this.annotationProcessors;
    }

    public @NonNull List<Processor<D, S>> getProcessors() {
        return this.processors;
    }

    public @NonNull ArgumentValidator<S, ST> getArgumentValidator() {
        return this.argumentValidator;
    }

    public @NonNull CommandExecutor<S> getCommandExecutor() {
        return this.commandExecutor;
    }

    public @NonNull SenderExtension<D, S> getSenderExtension() {
        return this.senderExtension;
    }

    public @NonNull SuggestionMapper<ST> getSuggestionMapper() {
        return this.suggestionMapper;
    }
}