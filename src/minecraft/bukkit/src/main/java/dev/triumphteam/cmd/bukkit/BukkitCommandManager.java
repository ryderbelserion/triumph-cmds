package dev.triumphteam.cmd.bukkit;

import dev.triumphteam.cmd.bukkit.impl.TriumphCommand;
import dev.triumphteam.cmd.bukkit.message.BukkitMessageKey;
import dev.triumphteam.cmd.core.CommandManager;
import dev.triumphteam.cmd.core.command.InternalRootCommand;
import dev.triumphteam.cmd.core.extension.registry.MessageRegistry;
import dev.triumphteam.cmd.core.extension.registry.RegistryContainer;
import dev.triumphteam.cmd.core.extension.sender.SenderExtension;
import dev.triumphteam.cmd.core.message.MessageKey;
import dev.triumphteam.cmd.core.processor.RootCommandProcessor;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public final class BukkitCommandManager<S> extends CommandManager<BukkitCommandManager<S>, BukkitCommandOptions<S>, CommandSender, S, String> {

    private final Map<String, TriumphCommand<S>> commands = new HashMap<>();
    private final JavaPlugin plugin;
    private final Server server;

    private BukkitCommandManager(
            @NonNull final JavaPlugin plugin,
            @NonNull final BukkitCommandOptions<S> commandOptions,
            @NonNull final RegistryContainer<CommandSender, S, String> registryContainer
    ) {
        super(commandOptions, registryContainer);

        this.plugin = plugin;
        this.server = this.plugin.getServer();

        // Register some defaults
        registerArgument(Player.class, (_, arg) -> this.server.getPlayer(arg));
        registerArgument(World.class, (_, arg) -> this.server.getWorld(arg));

        registerSuggestion(Player.class, (_) -> this.server.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList()));
    }

    /**
     * Creates a new instance of the {@link BukkitCommandManager}.
     * This method allows customization of the command manager through the provided builder.
     *
     * @param plugin          The {@link Plugin} instance associated with this command manager.
     * @param senderExtension The {@link SenderExtension} responsible for handling sender mapping and validation.
     * @param builder         A {@link Consumer} that configures the {@link BukkitCommandOptions.Builder} for the command manager.
     * @param <S>             The type of the sender that will be used in the command manager.
     * @return A new instance of the {@link BukkitCommandManager} configured with the given options.
     */
    @Contract("_, _, _ -> new")
    public static <S> @NonNull BukkitCommandManager<S> create(
            @NonNull final JavaPlugin plugin,
            @NonNull final SenderExtension<CommandSender, S> senderExtension,
            @NonNull final Consumer<BukkitCommandOptions.Builder<S>> builder
    ) {
        final RegistryContainer<CommandSender, S, String> registryContainer = new RegistryContainer<>();
        final BukkitCommandOptions.Builder<S> extensionBuilder = new BukkitCommandOptions.Builder<>();

        builder.accept(extensionBuilder);

        return new BukkitCommandManager<>(plugin, extensionBuilder.build(senderExtension), registryContainer);
    }

    /**
     * Creates a new instance of the {@link BukkitCommandManager}.
     * This factory adds all the defaults based on the default sender {@link CommandSender}.
     *
     * @param plugin The {@link Plugin} instance created.
     * @return A new instance of the {@link BukkitCommandManager}.
     */
    @Contract("_ -> new")
    public static @NonNull BukkitCommandManager<CommandSender> create(@NonNull final JavaPlugin plugin) {
        return create(plugin, _ -> {});
    }

    /**
     * Creates a new instance of {@link BukkitCommandManager} with default settings for Bukkit functionality.
     * The method accepts a builder to customize the command manager's options.
     *
     * @param plugin  The {@link Plugin} instance associated with this command manager.
     * @param builder A {@link Consumer} that configures the {@link BukkitCommandOptions.Builder} for the command manager.
     * @return A new instance of {@link BukkitCommandManager} configured with the provided options and defaults.
     */
    @Contract("_, _ -> new")
    public static @NonNull BukkitCommandManager<CommandSender> create(
            @NonNull final JavaPlugin plugin,
            @NonNull final Consumer<BukkitCommandOptions.Builder<CommandSender>> builder
    ) {
        final RegistryContainer<CommandSender, CommandSender, String> registryContainer = new RegistryContainer<>();
        final BukkitCommandOptions.Builder<CommandSender> extensionBuilder = new BukkitCommandOptions.Builder<>();

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
     * @param messageRegistry The {@link BukkitCommandManager} instance to set up.
     */
    private static void setUpDefaults(@NonNull final MessageRegistry<CommandSender> messageRegistry) {
        messageRegistry.register(MessageKey.UNKNOWN_COMMAND, (sender, context) -> sender.sendMessage("Unknown command: `" + context.getInvalidInput() + "`."));
        messageRegistry.register(MessageKey.TOO_MANY_ARGUMENTS, (sender, _) -> sender.sendMessage("Invalid usage."));
        messageRegistry.register(MessageKey.NOT_ENOUGH_ARGUMENTS, (sender, _) -> sender.sendMessage("Invalid usage."));
        messageRegistry.register(MessageKey.INVALID_ARGUMENT, (sender, context) -> sender.sendMessage("Invalid argument `" + context.getInvalidInput() + "` for type `" + context.getArgumentType().getSimpleName() + "`."));

        messageRegistry.register(BukkitMessageKey.NO_PERMISSION, (sender, _) -> sender.sendMessage("You do not have permission to perform this command."));
        messageRegistry.register(BukkitMessageKey.PLAYER_ONLY, (sender, _) -> sender.sendMessage("This command can only be used by players."));
        messageRegistry.register(BukkitMessageKey.CONSOLE_ONLY, (sender, _) -> sender.sendMessage("This command can only be used by the console."));
    }

    @Override
    protected @NonNull BukkitCommandManager<S> getThis() {
        return this;
    }

    @Override
    public void registerCommand(@NonNull final Object command) {
        final RootCommandProcessor<CommandSender, S, String> processor = new RootCommandProcessor<>(
                command,
                getRegistryContainer(),
                getCommandOptions()
        );

        final String name = processor.getName();

        // Get or add a command, then add its sub commands
        final TriumphCommand<S> bukkitCommand = this.commands.computeIfAbsent(name, _ -> createAndRegisterCommand(processor, name));

        final InternalRootCommand<CommandSender, S, String> rootCommand = bukkitCommand.getRootCommand();

        rootCommand.addCommands(command, processor.commands(rootCommand));

        // TODO: ALIASES
    }

    private @NonNull TriumphCommand<S> createAndRegisterCommand(
            @NonNull final RootCommandProcessor<CommandSender, S, String> processor,
            @NonNull final String name
    ) {
        final TriumphCommand<S> command = new TriumphCommand<>(processor);

        this.plugin.registerCommand(name, command);

        return command;
    }
}
