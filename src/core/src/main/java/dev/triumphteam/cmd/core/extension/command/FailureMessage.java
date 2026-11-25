package dev.triumphteam.cmd.core.extension.command;

import dev.triumphteam.cmd.core.message.MessageKey;
import dev.triumphteam.cmd.core.message.context.MessageContext;
import org.jspecify.annotations.NonNull;

public final class FailureMessage<C extends MessageContext> {

    private final MessageKey<C> messageKey;
    private final C context;

    public FailureMessage(@NonNull final MessageKey<C> messageKey, @NonNull final C context) {
        this.messageKey = messageKey;
        this.context = context;
    }

    public static @NonNull <C extends MessageContext> FailureMessage<C> of(@NonNull final MessageKey<C> messageKey, @NonNull final C context) {
        return new FailureMessage<>(messageKey, context);
    }

    public @NonNull MessageKey<C> getMessageKey() {
        return messageKey;
    }

    public @NonNull C getContext() {
        return context;
    }
}
