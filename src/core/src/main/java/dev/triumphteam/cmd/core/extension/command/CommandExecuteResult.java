package dev.triumphteam.cmd.core.extension.command;

import dev.triumphteam.cmd.core.extension.meta.CommandMeta;
import dev.triumphteam.cmd.core.message.MessageSender;
import org.jspecify.annotations.NonNull;

public interface CommandExecuteResult<S> {

    static <S> @NonNull CommandExecuteResult<S> success() {
        return new Success<>();
    }

    static <S> @NonNull CommandExecuteResult<S> failure(@NonNull final Failure<S> failure) {
        return failure;
    }

    @FunctionalInterface
    interface Failure<S> extends CommandExecuteResult<S> {

        void sendMessage(@NonNull final MessageSender<S> messageSender, @NonNull final S sender, @NonNull final CommandMeta meta);
    }

    class Success<S> implements CommandExecuteResult<S> {}
}