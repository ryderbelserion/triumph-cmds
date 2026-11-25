package dev.triumphteam.cmd.core.extension.defaults;

import dev.triumphteam.cmd.core.extension.command.CommandExecutor;
import dev.triumphteam.cmd.core.extension.meta.CommandMeta;
import dev.triumphteam.cmd.core.extension.registry.MessageRegistry;
import org.jspecify.annotations.NonNull;
import java.lang.reflect.Method;
import java.util.List;

public final class DefaultCommandExecutor<S> implements CommandExecutor<S> {

    @Override
    public void execute(
            @NonNull final CommandMeta meta,
            @NonNull final MessageRegistry<S> messageRegistry,
            @NonNull final S sender,
            @NonNull final Object instance,
            @NonNull final Method method,
            @NonNull final List<Object> arguments
    ) throws Throwable {
        // Executes and handles the result.
        handleResult(meta, messageRegistry, sender, method.invoke(instance, arguments.toArray()));
    }
}