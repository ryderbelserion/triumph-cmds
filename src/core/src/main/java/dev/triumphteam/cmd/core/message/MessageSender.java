package dev.triumphteam.cmd.core.message;

import dev.triumphteam.cmd.core.message.context.MessageContext;
import org.jspecify.annotations.NonNull;

public interface MessageSender<S> {

    <C extends MessageContext> void sendMessage(
            @NonNull final MessageKey<C> key,
            @NonNull final S sender,
            @NonNull final C context
    );
}