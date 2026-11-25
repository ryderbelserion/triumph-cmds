package dev.triumphteam.cmd.core.message;

import dev.triumphteam.cmd.core.message.context.InvalidArgumentContext;
import dev.triumphteam.cmd.core.message.context.InvalidCommandContext;
import dev.triumphteam.cmd.core.message.context.MessageContext;
import dev.triumphteam.cmd.core.message.context.SyntaxMessageContext;
import org.jspecify.annotations.NonNull;

/**
 * {@link MessageKey} is used for easier registering of messages with different {@link MessageContext}.
 *
 * @param <C> A {@link MessageContext} type, this allows for better customization of the messages.
 */
public class MessageKey<C extends MessageContext> extends ContextualKey<C> {

    // Default keys
    public static final MessageKey<InvalidCommandContext> UNKNOWN_COMMAND = of("unknown.command", InvalidCommandContext.class);
    public static final MessageKey<SyntaxMessageContext> TOO_MANY_ARGUMENTS = of("too.many.arguments", SyntaxMessageContext.class);
    public static final MessageKey<SyntaxMessageContext> NOT_ENOUGH_ARGUMENTS = of("not.enough.arguments", SyntaxMessageContext.class);
    public static final MessageKey<InvalidArgumentContext> INVALID_ARGUMENT = of("invalid.argument", InvalidArgumentContext.class);

    protected MessageKey(@NonNull final String key, @NonNull final Class<C> type) {
        super(key, type);
    }

    /**
     * Factory method for creating a {@link MessageKey}.
     *
     * @param key  The value of the key, normally separated by <code>.</code>.
     * @param type The {@link MessageContext} type.
     * @param <C>  Generic {@link MessageContext} type.
     * @return A new {@link MessageKey} for a specific {@link MessageContext}.
     */
    public static <C extends MessageContext> @NonNull MessageKey<C> of(@NonNull final String key, @NonNull final Class<C> type) {
        return new MessageKey<>(key, type);
    }
}