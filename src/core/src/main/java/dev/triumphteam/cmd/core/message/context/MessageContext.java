package dev.triumphteam.cmd.core.message.context;

import dev.triumphteam.cmd.core.extension.meta.CommandMeta;
import org.jspecify.annotations.NonNull;

/**
 * Contains specific data for error handling.
 */
public interface MessageContext {

    /**
     * @return The command's meta.
     */
    @NonNull CommandMeta getMeta();
}