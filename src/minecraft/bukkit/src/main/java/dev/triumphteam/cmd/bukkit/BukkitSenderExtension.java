package dev.triumphteam.cmd.bukkit;

import com.google.common.collect.ImmutableSet;
import dev.triumphteam.cmd.bukkit.message.BukkitMessageKey;
import dev.triumphteam.cmd.core.extension.ValidationResult;
import dev.triumphteam.cmd.core.extension.meta.CommandMeta;
import dev.triumphteam.cmd.core.extension.sender.SenderExtension;
import dev.triumphteam.cmd.core.message.MessageKey;
import dev.triumphteam.cmd.core.message.context.MessageContext;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;
import java.util.Set;

/**
 * Simple mapper than returns itself.
 */
class BukkitSenderExtension implements SenderExtension.Default<CommandSender> {

    @Override
    public @NonNull Set<Class<? extends @NonNull CommandSender>> getAllowedSenders() {
        return ImmutableSet.of(CommandSender.class, ConsoleCommandSender.class, Player.class);
    }

    @Override
    public @NonNull ValidationResult<@NonNull MessageKey<@NonNull MessageContext>> validate(
            @NonNull final CommandMeta meta,
            @NonNull final Class<?> allowedSender,
            @NonNull final CommandSender sender
    ) {
        if (Player.class.isAssignableFrom(allowedSender) && !(sender instanceof Player)) {
            return invalid(BukkitMessageKey.PLAYER_ONLY);
        }

        if (ConsoleCommandSender.class.isAssignableFrom(allowedSender) && !(sender instanceof ConsoleCommandSender)) {
            return invalid(BukkitMessageKey.CONSOLE_ONLY);
        }

        return valid();
    }
}