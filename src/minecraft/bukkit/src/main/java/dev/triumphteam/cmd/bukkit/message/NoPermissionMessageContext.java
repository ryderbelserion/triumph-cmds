package dev.triumphteam.cmd.bukkit.message;

import dev.triumphteam.cmd.bukkit.CommandPermission;
import dev.triumphteam.cmd.core.extension.meta.CommandMeta;
import dev.triumphteam.cmd.core.message.context.BasicMessageContext;
import org.jspecify.annotations.NonNull;

public final class NoPermissionMessageContext extends BasicMessageContext {

    private final CommandPermission permission;

    public NoPermissionMessageContext(
            @NonNull final CommandMeta meta,
            @NonNull final CommandPermission permission
    ) {
        super(meta);
        
        this.permission = permission;
    }

    public @NonNull CommandPermission getPermission() {
        return this.permission;
    }
}