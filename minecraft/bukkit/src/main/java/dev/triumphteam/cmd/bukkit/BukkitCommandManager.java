/**
 * MIT License
 * <p>
 * Copyright (c) 2019-2021 Matt
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package dev.triumphteam.cmd.bukkit;

import com.ryderbelserion.cmd.core.TriumphManager;
import com.ryderbelserion.cmd.core.TriumphProvider;
import dev.triumphteam.cmd.bukkit.message.BukkitMessageKey;
import dev.triumphteam.cmd.core.CommandManager;
import dev.triumphteam.cmd.core.command.RootCommand;
import dev.triumphteam.cmd.core.exceptions.CommandRegistrationException;
import dev.triumphteam.cmd.core.extension.registry.MessageRegistry;
import dev.triumphteam.cmd.core.extension.registry.RegistryContainer;
import dev.triumphteam.cmd.core.extension.sender.SenderExtension;
import dev.triumphteam.cmd.core.enums.Mode;
import dev.triumphteam.cmd.core.message.MessageKey;
import dev.triumphteam.cmd.core.processor.RootCommandProcessor;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.audience.Audience;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public final class BukkitCommandManager<S> extends CommandManager<CommandSender, S, BukkitCommandOptions<S>> implements TriumphManager {

    private final Plugin plugin;
    private final RegistryContainer<CommandSender, S> registryContainer;

    private final Map<String, BukkitCommand<S>> commands = new HashMap<>();

    private BukkitCommandManager(
            @NotNull final Plugin plugin,
            @NotNull final BukkitCommandOptions<S> commandOptions,
            @NotNull final RegistryContainer<CommandSender, S> registryContainer
    ) {
        super(commandOptions);

        this.plugin = plugin;
        this.registryContainer = registryContainer;

        final Server server = this.plugin.getServer();

        registerArgument(Material.class, (sender, arg) -> Material.matchMaterial(arg)); //todo() add support for ItemType as matchMaterial is dead
        registerArgument(Player.class, (sender, arg) -> server.getPlayer(arg)); //todo() add support for offline players
        registerArgument(World.class, (sender, arg) -> server.getWorld(arg));

        registerSuggestion(Player.class, (sender, arguments) -> server.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList()));

        TriumphProvider.register(this);
    }

    /**
     * Creates a new instance of the {@link BukkitCommandManager}.
     *
     * @param plugin the {@link Plugin} instance created.
     * @return a new instance of the {@link BukkitCommandManager}.
     */
    @Contract("_, _, _ -> new")
    public static <S> @NotNull BukkitCommandManager<S> create(
            @NotNull final Plugin plugin,
            @NotNull final SenderExtension<CommandSender, S> senderExtension,
            @NotNull final Consumer<BukkitCommandOptions.Builder<S>> builder
    ) {
        final RegistryContainer<CommandSender, S> registryContainer = new RegistryContainer<>();

        final BukkitCommandOptions.Builder<S> extensionBuilder = new BukkitCommandOptions.Builder<>(registryContainer);

        builder.accept(extensionBuilder);

        return new BukkitCommandManager<>(plugin, extensionBuilder.build(senderExtension), registryContainer);
    }

    /**
     * Creates a new instance of the {@link BukkitCommandManager}.
     * This factory adds all the defaults based on the default sender {@link CommandSender}.
     *
     * @param plugin the {@link Plugin} instance created.
     * @return a new instance of the {@link BukkitCommandManager}.
     */
    @Contract("_ -> new")
    public static @NotNull BukkitCommandManager<CommandSender> create(@NotNull final Plugin plugin) {
        return create(plugin, builder -> {});
    }

    /**
     * Creates a new instance of the {@link BukkitCommandManager}.
     * This factory adds all the defaults based on the default sender {@link CommandSender}.
     *
     * @param plugin the {@link Plugin} instance created.
     * @return a new instance of the {@link BukkitCommandManager}.
     */
    @Contract("_, _ -> new")
    public static @NotNull BukkitCommandManager<CommandSender> create(
            @NotNull final Plugin plugin,
            @NotNull final Consumer<BukkitCommandOptions.Builder<CommandSender>> builder
    ) {
        final RegistryContainer<CommandSender, CommandSender> registryContainer = new RegistryContainer<>();
        final BukkitCommandOptions.Builder<CommandSender> extensionBuilder = new BukkitCommandOptions.Builder<>(registryContainer);

        // Setup defaults for Bukkit
        final MessageRegistry<CommandSender> messageRegistry = registryContainer.getMessageRegistry();

        setUpDefaults(messageRegistry);

        // Then accept configured values
        builder.accept(extensionBuilder);

        return new BukkitCommandManager<>(plugin, extensionBuilder.build(new BukkitSenderExtension()), registryContainer);
    }

    /**
     * Sets up all the default values for the Bukkit implementation.
     *
     * @param messageRegistry the {@link BukkitCommandManager} instance to set up.
     */
    private static void setUpDefaults(@NotNull final MessageRegistry<CommandSender> messageRegistry) {
        messageRegistry.register(MessageKey.UNKNOWN_COMMAND, (sender, context) -> sender.sendMessage("Unknown command: `" + context.getInvalidInput() + "`."));
        messageRegistry.register(MessageKey.TOO_MANY_ARGUMENTS, (sender, context) -> sender.sendMessage("Invalid usage."));
        messageRegistry.register(MessageKey.NOT_ENOUGH_ARGUMENTS, (sender, context) -> sender.sendMessage("Invalid usage."));
        messageRegistry.register(MessageKey.INVALID_ARGUMENT, (sender, context) -> sender.sendMessage("Invalid argument `" + context.getInvalidInput() + "` for type `" + context.getArgumentType().getSimpleName() + "`."));

        messageRegistry.register(BukkitMessageKey.NO_PERMISSION, (sender, context) -> sender.sendMessage("You do not have permission to perform this command."));
        messageRegistry.register(BukkitMessageKey.PLAYER_ONLY, (sender, context) -> sender.sendMessage("This command can only be used by players."));
        messageRegistry.register(BukkitMessageKey.CONSOLE_ONLY, (sender, context) -> sender.sendMessage("This command can only be used by the console."));
    }

    @Override
    public void registerCommand(@NotNull final Object command) {
        final RootCommandProcessor<CommandSender, S> processor = new RootCommandProcessor<>(
                command,
                getRegistryContainer(),
                getCommandOptions()
        );

        final String name = processor.getName();

        // Get or add command, then add its sub commands
        final BukkitCommand<S> bukkitCommand = commands.computeIfAbsent(name, it -> createAndRegisterCommand(processor, name));

        final RootCommand<CommandSender, S> rootCommand = bukkitCommand.getRootCommand();

        rootCommand.addCommands(command, processor.commands(rootCommand));

        // TODO: ALIASES
    }

    @Override
    public void registerNodes(@NotNull final List<String> nodes, @NotNull final String description, @NotNull final Mode mode) {
        final PluginManager manager = this.plugin.getServer().getPluginManager();

        nodes.forEach(node -> {
            final Permission permission = manager.getPermission(node);

            if (permission != null) return;

            PermissionDefault permissionDefault = PermissionDefault.OP;

            switch (mode) {
                case TRUE -> permissionDefault = PermissionDefault.TRUE;
                case FALSE -> permissionDefault = PermissionDefault.FALSE;
                case NOT_OP -> permissionDefault = PermissionDefault.NOT_OP;
            }

            manager.addPermission(new Permission(node, description, permissionDefault));
        });
    }

    @Override
    public boolean hasPermission(@NotNull final Audience audience, @NotNull final String permission) {
        final CommandSender sender = (CommandSender) audience;

        return sender instanceof ConsoleCommandSender || sender.hasPermission(permission);
    }

    public Map<String, BukkitCommand<S>> getCommands() {
        return Collections.unmodifiableMap(this.commands);
    }

    @Override
    protected @NotNull RegistryContainer<CommandSender, S> getRegistryContainer() {
        return this.registryContainer;
    }

    private @NotNull BukkitCommand<S> createAndRegisterCommand(
            @NotNull final RootCommandProcessor<CommandSender, S> processor,
            @NotNull final String name
    ) {
        final BukkitCommand<S> newCommand = new BukkitCommand<>(processor);

        this.plugin.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            final Commands registry = event.registrar();

            registry.register(newCommand.getName(), newCommand.getDescription(), newCommand);
        });

        return newCommand;
    }
}