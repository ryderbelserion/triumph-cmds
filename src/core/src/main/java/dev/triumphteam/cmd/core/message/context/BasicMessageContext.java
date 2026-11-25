package dev.triumphteam.cmd.core.message.context;

import dev.triumphteam.cmd.core.extension.meta.CommandMeta;
import org.jspecify.annotations.NonNull;

public class BasicMessageContext implements MessageContext {

    private final CommandMeta meta;

    public BasicMessageContext(@NonNull final CommandMeta meta) {
        this.meta = meta;
    }

    @Override
    public @NonNull CommandMeta getMeta() {
        return this.meta;
    }
}