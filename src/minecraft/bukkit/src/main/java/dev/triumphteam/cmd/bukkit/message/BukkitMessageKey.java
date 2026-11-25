package dev.triumphteam.cmd.bukkit.message;

import dev.triumphteam.cmd.core.message.MessageKey;
import dev.triumphteam.cmd.core.message.context.MessageContext;
import org.jspecify.annotations.NonNull;

/**
 * {@link BukkitMessageKey} is used for easier registering of messages with different {@link MessageContext}.
 *
 * @param <C> A {@link MessageContext} type, this allows for better customization of the messages.
 */
public final class BukkitMessageKey<C extends MessageContext> extends MessageKey<C> {

    // Default keys
    public static final BukkitMessageKey<NoPermissionMessageContext> NO_PERMISSION = new BukkitMessageKey<>("no.permission", NoPermissionMessageContext.class);
    public static final BukkitMessageKey<MessageContext> PLAYER_ONLY = new BukkitMessageKey<>("player.only", MessageContext.class);
    public static final BukkitMessageKey<MessageContext> CONSOLE_ONLY = new BukkitMessageKey<>("console.only", MessageContext.class);

    private BukkitMessageKey(@NonNull final String key, @NonNull final Class<C> type) {
        super(key, type);
    }

    @Override
    public @NonNull String toString() {
        return "BukkitMessageKey{super=" + super.toString() + "}";
    }
}