package dev.triumphteam.cmd.bukkit;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.PluginManager;
import org.jspecify.annotations.NonNull;
import org.jetbrains.annotations.Nullable;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Data holder for the command's permission.
 * Including its default state and a description.
 */
public final class CommandPermission {

    private final List<String> nodes;
    private final PermissionDefault permissionDefault;
    private final String description;

    public CommandPermission(
            @NonNull final List<String> nodes,
            @NonNull final String description,
            @NonNull final PermissionDefault permissionDefault
    ) {
        this.nodes = nodes;
        this.description = description;
        this.permissionDefault = permissionDefault;

        register();
    }

    /**
     * Checks whether the {@link CommandSender} has the (nullable) {@link CommandPermission}.
     * <p>
     * The method simply checks if any of the following two things are {@code true}:
     * <ul>
     *     <li>The permission is {@code null}</li>
     *     <li>The sender has the permission</li>
     * </ul>
     *
     * @param sender     The main command sender.
     * @param permission The permission.
     * @return Whether the sender has permission to run the command.
     */
    public static boolean hasPermission(
            @NonNull final CommandSender sender,
            final @Nullable CommandPermission permission
    ) {
        return permission == null || permission.hasPermission(sender);
    }

    public @NonNull CommandPermission child(
            @NonNull final List<String> nodes,
            @NonNull final String description,
            @NonNull final PermissionDefault permissionDefault
    ) {
        final List<String> newNodes = this.nodes.stream()
                .flatMap(parent -> nodes.stream().map(node -> parent + "." + node))
                .collect(Collectors.toList());

        return new CommandPermission(newNodes, description, permissionDefault);
    }

    /**
     * Register the {@link Permission} to the server.
     */
    public void register() {
        final PluginManager pluginManager = Bukkit.getPluginManager();

        this.nodes.forEach(node -> {
            // Don't register if already registered
            final Permission permission = pluginManager.getPermission(node);

            if (permission != null) return;

            pluginManager.addPermission(new Permission(node, this.description, this.permissionDefault));
        });
    }

    /**
     * Gets the permission nodes.
     *
     * @return The permission nodes.
     */
    public @NonNull List<String> getNodes() {
        return this.nodes;
    }

    /**
     * Checks if the {@link CommandSender} has the permission to run the command.
     *
     * @param sender The main command sender.
     * @return Whether the sender has permission to run the command.
     */
    public boolean hasPermission(@NonNull final CommandSender sender) {
        return this.nodes.stream().anyMatch(sender::hasPermission);
    }

    @Override
    public String toString() {
        return "CommandPermission{" +
                "nodes=" + this.nodes +
                '}';
    }
}