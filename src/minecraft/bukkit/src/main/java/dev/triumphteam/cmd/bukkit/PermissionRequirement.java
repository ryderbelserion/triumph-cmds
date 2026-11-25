package dev.triumphteam.cmd.bukkit;

import dev.triumphteam.cmd.bukkit.message.BukkitMessageKey;
import dev.triumphteam.cmd.bukkit.message.NoPermissionMessageContext;
import dev.triumphteam.cmd.core.extension.meta.CommandMeta;
import dev.triumphteam.cmd.core.extension.registry.MessageRegistry;
import dev.triumphteam.cmd.core.extension.sender.SenderMapper;
import dev.triumphteam.cmd.core.requirement.Requirement;
import org.bukkit.command.CommandSender;
import org.jspecify.annotations.NonNull;

final class PermissionRequirement<S> implements Requirement<CommandSender, S> {

    private final CommandPermission permission;

    public PermissionRequirement(@NonNull final CommandPermission permission) {
        this.permission = permission;
    }

    @Override
    public boolean test(
            @NonNull final S sender,
            @NonNull final CommandMeta meta,
            @NonNull final SenderMapper<CommandSender, S> senderMapper
    ) {
        return this.permission.hasPermission(senderMapper.mapBackwards(sender));
    }

    @Override
    public void onDeny(
            @NonNull final S sender,
            @NonNull final MessageRegistry<S> messageRegistry,
            @NonNull final CommandMeta meta
    ) {
        messageRegistry.sendMessage(BukkitMessageKey.NO_PERMISSION, sender, new NoPermissionMessageContext(meta, this.permission));
    }

    @Override
    public String toString() {
        return "PermissionRequirement{" +
                "permission=" + this.permission +
                '}';
    }
}