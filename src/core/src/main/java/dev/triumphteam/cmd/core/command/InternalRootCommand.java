package dev.triumphteam.cmd.core.command;

import dev.triumphteam.cmd.core.exceptions.CommandExecutionException;
import dev.triumphteam.cmd.core.extension.meta.MetaKey;
import dev.triumphteam.cmd.core.processor.RootCommandProcessor;
import org.jspecify.annotations.NonNull;
import java.lang.reflect.InvocationTargetException;
import java.util.Deque;
import java.util.List;

public class InternalRootCommand<D, S, ST> extends InternalParentCommand<D, S, ST> {

    private final String name;
    private final List<String> aliases;
    private final String description;
    private final String syntax;

    public InternalRootCommand(@NonNull final RootCommandProcessor<D, S, ST> processor) {
        super(processor);

        this.name = processor.getName();
        this.description = getMeta().getOrDefault(MetaKey.DESCRIPTION, "");
        this.aliases = processor.getAliases();
        this.syntax = "/" + name;
    }

    public void execute(
            @NonNull final S sender,
            @NonNull final Deque<String> arguments
    ) {
        // Test all requirements before continuing
        if (!getSettings().testRequirements(getMessageRegistry(), sender, getMeta(), getSenderExtension())) return;

        // Executing the command and catch all exceptions to rethrow with a better message
        try {
            findAndExecute(sender, null, arguments);
        } catch (final Throwable exception) {
            throw new CommandExecutionException("An error occurred while executing the command").initCause(exception instanceof InvocationTargetException ? exception.getCause() : exception);
        }
    }

    @Override
    public @NonNull String getName() {
        return this.name;
    }

    @Override
    public @NonNull String getDescription() {
        return this.description;
    }

    @Override
    public @NonNull List<String> getAliases() {
        return this.aliases;
    }

    @Override
    public @NonNull String getSyntax() {
        return this.syntax;
    }

    @Override
    public boolean hasArguments() {
        return false;
    }
}