package dev.triumphteam.cmd.bukkit.impl;

import dev.triumphteam.cmd.core.command.InternalRootCommand;
import dev.triumphteam.cmd.core.extension.sender.SenderExtension;
import dev.triumphteam.cmd.core.processor.RootCommandProcessor;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.CommandSender;
import org.jspecify.annotations.NonNull;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collection;

public final class TriumphCommand<S> implements BasicCommand {

    private final InternalRootCommand<CommandSender, S, String> rootCommand;
    private final SenderExtension<CommandSender, S> senderExtension;

    public TriumphCommand(@NonNull final RootCommandProcessor<CommandSender, S, String> processor) {
        this.rootCommand = new InternalRootCommand<>(processor);
        this.senderExtension = processor.getCommandOptions().getCommandExtensions().getSenderExtension();
    }

    @Override
    public void execute(@NonNull final CommandSourceStack stack, @NonNull final String @NonNull [] args) {
        this.rootCommand.execute(this.senderExtension.map(stack.getSender()), new ArrayDeque<>(Arrays.asList(args)));
    }

    @Override
    public @NonNull Collection<String> suggest(@NonNull final CommandSourceStack stack, @NonNull final String @NonNull [] args) {
        return this.rootCommand.suggestions(this.senderExtension.map(stack.getSender()), new ArrayDeque<>(Arrays.asList(args)));
    }

    //todo() move permission support to the `canUse` method.

    public @NonNull InternalRootCommand<CommandSender, S, String> getRootCommand() {
        return this.rootCommand;
    }
}