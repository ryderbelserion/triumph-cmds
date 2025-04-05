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
package dev.triumphteam.cmds.simple;

import dev.triumphteam.cmd.core.CommandManager;
import dev.triumphteam.cmd.core.command.RootCommand;
import dev.triumphteam.cmd.core.extension.CommandOptions;
import dev.triumphteam.cmd.core.extension.meta.CommandMeta;
import dev.triumphteam.cmd.core.extension.registry.RegistryContainer;
import dev.triumphteam.cmd.core.extension.sender.SenderExtension;
import dev.triumphteam.cmd.core.message.MessageKey;
import dev.triumphteam.cmd.core.message.context.InvalidCommandContext;
import dev.triumphteam.cmd.core.processor.RootCommandProcessor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public final class SimpleCommandManager<S> extends CommandManager<S, S, CommandOptions<S, S>> {

    private final Map<String, RootCommand<S, S>> commands = new HashMap<>();

    private final RegistryContainer<S, S> registryContainer;

    private SimpleCommandManager(
            @NotNull final CommandOptions<S, S> commandOptions,
            @NotNull final RegistryContainer<S, S> registryContainer
    ) {
        super(commandOptions);

        this.registryContainer = registryContainer;
    }

    @Contract("_, _ -> new")
    public static <S> @NotNull SimpleCommandManager<S> create(
            @NotNull final SenderExtension<S, S> senderExtension,
            @NotNull final Consumer<SimpleOptionsBuilder<S>> builder
    ) {
        final RegistryContainer<S, S> registryContainer = new RegistryContainer<>();
        final SimpleOptionsBuilder<S> extensionBuilder = new SimpleOptionsBuilder<>(registryContainer);

        builder.accept(extensionBuilder);

        return new SimpleCommandManager<>(extensionBuilder.build(senderExtension), registryContainer);
    }

    @Override
    public void registerCommand(@NotNull final Object command) {
        final RootCommandProcessor<S, S> processor = new RootCommandProcessor<>(
                command,
                getRegistryContainer(),
                getCommandOptions()
        );

        final String name = processor.getName();

        final RootCommand<S, S> rootCommand = this.commands.computeIfAbsent(name, it -> new RootCommand<>(processor));

        rootCommand.addCommands(command, processor.commands(rootCommand));

        processor.getAliases().forEach(it -> this.commands.putIfAbsent(it, rootCommand));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected @NotNull RegistryContainer<S, S> getRegistryContainer() {
        return this.registryContainer;
    }

    @Override
    public void unregisterCommand(@NotNull final Object command) {
        // TODO add a remove functionality
    }

    /**
     * Execute the commands given the passed arguments.
     *
     * @param sender the provided sender.
     * @param args the provided arguments.
     */
    public void executeCommand(@NotNull final S sender, @NotNull final List<String> args) {
        if (args.isEmpty()) return;

        final String commandName = args.getFirst();

        final RootCommand<S, S> command = this.commands.get(commandName);

        if (command == null) {
            this.registryContainer.getMessageRegistry().sendMessage(
                    MessageKey.UNKNOWN_COMMAND,
                    sender,
                    // Empty meta
                    new InvalidCommandContext(new CommandMeta.Builder(null).build(), commandName)
            );

            return;
        }

        command.execute(sender, null, new ArrayDeque<>(args.subList(1, args.size())));
    }
}