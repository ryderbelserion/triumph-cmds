package dev.triumphteam.cmd.core.command;

import dev.triumphteam.cmd.core.exceptions.CommandRegistrationException;
import dev.triumphteam.cmd.core.extension.SuggestionMapper;
import dev.triumphteam.cmd.core.extension.command.Settings;
import dev.triumphteam.cmd.core.extension.meta.CommandMeta;
import dev.triumphteam.cmd.core.extension.registry.MessageRegistry;
import dev.triumphteam.cmd.core.extension.sender.SenderExtension;
import dev.triumphteam.cmd.core.message.MessageKey;
import dev.triumphteam.cmd.core.message.context.InvalidCommandContext;
import dev.triumphteam.cmd.core.processor.CommandProcessor;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import static java.util.Collections.emptyList;

/**
 * A parent command means it's a simple holder of other commands.
 * The commands can either be a parent as well or simple sub commands.
 *
 * @param <S> The sender type.
 */
public abstract class InternalParentCommand<D, S, ST> implements InternalCommand<D, S, ST> {

    private final Map<String, InternalCommand<D, S, ST>> commands = new HashMap<>();
    private final Map<String, InternalCommand<D, S, ST>> commandAliases = new HashMap<>();
    private final CommandMeta meta;
    private final Settings<D, S> settings;
    private final MessageRegistry<S> messageRegistry;
    private final SenderExtension<D, S> senderExtension;
    private final SuggestionMapper<ST> suggestionMapper;

    public InternalParentCommand(@NonNull final CommandProcessor<D, S, ST> processor) {
        final Settings.Builder<D, S> settingsBuilder = new Settings.Builder<>();

        processor.captureRequirements(settingsBuilder);

        this.meta = processor.createMeta(settingsBuilder);

        this.messageRegistry = processor.getRegistryContainer().getMessageRegistry();
        this.senderExtension = processor.getCommandOptions().getCommandExtensions().getSenderExtension();
        this.suggestionMapper = processor.getCommandOptions().getCommandExtensions().getSuggestionMapper();

        this.settings = settingsBuilder.build();
    }

    /**
     * Add a new command to the maps.
     *
     * @param instance The instance of the command the commands came from.
     * @param commands A list of command to be added.
     */
    public void addCommands(
            @NonNull final Object instance,
            @NonNull final List<InternalCommand<D, S, ST>> commands
    ) {
        for (final InternalCommand<D, S, ST> command : commands) {
            // If it's a parent command with an argument, we add it
            if (command instanceof InternalBranchCommand && command.hasArguments()) {
                if (this.commands.containsKey(InternalCommand.PARENT_CMD_WITH_ARGS_NAME)) {
                    throw new CommandRegistrationException("Only one inner command with argument is allowed per command", instance.getClass());
                }
            }

            // Normal commands are added here
            this.commands.put(command.getName(), command);

            for (final String alias : command.getAliases()) {
                this.commandAliases.put(alias, command);
            }
        }
    }

    protected void findAndExecute(
            @NonNull final S sender,
            @Nullable final Supplier<Object> instanceSupplier,
            @NonNull final Deque<String> arguments
    ) throws Throwable {
        final InternalCommand<D, S, ST> command = findCommand(sender, arguments, true);

        if (command == null) return;

        // Executing the command and catch all exceptions to rethrow with a better message
        if (command instanceof InternalBranchCommand<D, S, ST> result) {
            result.execute(sender, instanceSupplier, arguments);

            return;
        }

        final InternalLeafCommand<D, S, ST> leafCommand = (InternalLeafCommand<D, S, ST>) command;

        leafCommand.execute(sender, instanceSupplier, leafCommand.mapArguments(arguments));
    }

    public @NonNull List<ST> suggestions(
            @NonNull final S sender,
            @NonNull final Deque<String> arguments
    ) {
        final String argument = arguments.peek();
        if (argument == null) return emptyList();

        final InternalCommand<D, S, ST> command = findCommand(sender, arguments, false);

        if (command == null) {
            return this.suggestionMapper.map(
                    this.commands.entrySet().stream()
                            // Remove the default command from the list.
                            .filter(it -> !it.getValue().isDefault())
                            // Filter commands the sender can't see.
                            .filter(it -> it.getValue().getCommandSettings().testRequirements(sender, this.meta, this.senderExtension))
                            // Commands that match what the sender is typing.
                            .filter(it -> it.getKey().startsWith(argument))
                            // Only use the names.
                            .map(Map.Entry::getKey)
                            .collect(Collectors.toList())
            );
        }

        if (command instanceof InternalBranchCommand<D, S, ST> result) {
            return result.suggestions(sender, arguments);
        }

        if (!(command instanceof InternalLeafCommand<D, S, ST> leaf)) {
            return emptyList();
        }

        return leaf.suggestions(sender, new ArrayList<>(arguments));
    }


    public @Nullable InternalCommand<D, S, ST> findCommand(
            @NonNull final S sender,
            @NonNull final Deque<String> arguments,
            final boolean sendMessage
    ) {
        final String name = arguments.peek();

        // Instant check for default
        final InternalCommand<D, S, ST> defaultCommand = getCommandByName(InternalCommand.DEFAULT_CMD_NAME);

        // No argument passed
        if (name == null) {
            // No default command found, send a message and return null
            // If there is a default command, then return it
            if (defaultCommand == null && sendMessage) {
                this.messageRegistry.sendMessage(MessageKey.UNKNOWN_COMMAND, sender, new InvalidCommandContext(this.meta, getSyntax(), ""));
            }

            return defaultCommand;
        }

        final InternalCommand<D, S, ST> command = safelyGetCommandByName(name);
        if (command != null) {
            // Pop the command out of the argument list and returns it
            arguments.pop();
            return command;
        }

        if (defaultCommand == null || !defaultCommand.hasArguments()) {
            // No command found with the name [name]
            final InternalCommand<D, S, ST> parentCommandWithArgument = getCommandByName(InternalCommand.PARENT_CMD_WITH_ARGS_NAME);

            if (parentCommandWithArgument == null && sendMessage) {
                this.messageRegistry.sendMessage(MessageKey.UNKNOWN_COMMAND, sender, new InvalidCommandContext(this.meta, getSyntax(), name));
            }

            // Don't pop because it'll be the argument
            return parentCommandWithArgument;
        }

        // Default command is never null here
        return defaultCommand;
    }

    public @Nullable InternalCommand<D, S, ST> getCommand(@NonNull final String name) {
        return this.commands.get(name);
    }

    @Override
    public @NonNull Settings<D, S> getCommandSettings() {
        return this.settings;
    }

    @Override
    public boolean isDefault() {
        return false;
    }

    @Override
    public boolean isHidden() {
        return false;
    }

    public @NonNull Map<String, InternalCommand<D, S, ST>> getCommands() {
        return this.commands;
    }

    protected @Nullable InternalCommand<D, S, ST> safelyGetCommandByName(@NonNull final String key) {
        // Don't let the default command be retrieved by the name.
        if (key.equals(InternalCommand.DEFAULT_CMD_NAME)) return null;

        if (key.equals(InternalCommand.PARENT_CMD_WITH_ARGS_NAME)) return null;

        return getCommandByName(key);
    }

    protected @Nullable InternalCommand<D, S, ST> getCommandByName(@NonNull final String key) {
        return this.commands.getOrDefault(key, this.commandAliases.get(key));
    }

    @Override
    public @NonNull CommandMeta getMeta() {
        return this.meta;
    }

    protected @NonNull MessageRegistry<S> getMessageRegistry() {
        return this.messageRegistry;
    }

    protected @NonNull Settings<D, S> getSettings() {
        return this.settings;
    }

    protected @NonNull SenderExtension<D, S> getSenderExtension() {
        return this.senderExtension;
    }
}