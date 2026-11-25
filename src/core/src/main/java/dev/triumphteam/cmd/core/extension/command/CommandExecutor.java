package dev.triumphteam.cmd.core.extension.command;

import dev.triumphteam.cmd.core.extension.meta.CommandMeta;
import dev.triumphteam.cmd.core.extension.registry.MessageRegistry;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import java.lang.reflect.Method;
import java.util.List;

public interface CommandExecutor<S> {

    void execute(
            @NonNull final CommandMeta meta,
            @NonNull final MessageRegistry<S> messageRegistry,
            @NonNull final S sender,
            @NonNull final Object instance,
            @NonNull final Method method,
            @NonNull final List<Object> arguments
    ) throws Throwable;

    default void handleResult(
            @NonNull final CommandMeta meta,
            @NonNull final MessageRegistry<S> messageRegistry,
            @NonNull final S sender,
            @Nullable final Object result
    ) {
        // We don't care about nulls.
        if (result == null) return;

        // We don't care if the return is not a CommandExecuteResult.
        if (!(result instanceof CommandExecuteResult)) return;

        // We also don't care if everything went well.
        if (!(result instanceof CommandExecuteResult.Failure failure)) return;

        try {
            // If we can't cast, we can just ignore it.
            failure.sendMessage(messageRegistry, sender, meta);
        } catch (ClassCastException ignored) {}
    }
}