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

import dev.triumphteam.cmd.core.command.RootCommand;
import dev.triumphteam.cmd.core.extension.sender.SenderExtension;
import dev.triumphteam.cmd.core.processor.RootCommandProcessor;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public final class BukkitCommand<S> implements BasicCommand {

    private final RootCommand<CommandSender, S> rootCommand;
    private final SenderExtension<CommandSender, S> senderExtension;

    private final String name;
    private final String description;
    private final List<String> aliases;

    BukkitCommand(@NotNull final RootCommandProcessor<CommandSender, S> processor) {
        this.name = processor.getName();
        this.description = processor.getDescription();
        this.aliases = processor.getAliases();

        this.rootCommand = new RootCommand<>(processor);
        this.senderExtension = processor.getCommandOptions().getCommandExtensions().getSenderExtension();
    }

    @Override
    public void execute(@NotNull CommandSourceStack source, @NotNull String @NotNull [] args) {
        this.rootCommand.execute(
                this.senderExtension.map(source.getSender()),
                null,
                new ArrayDeque<>(Arrays.asList(args))
        );
    }

    @Override
    public @NotNull Collection<String> suggest(@NotNull CommandSourceStack source, @NotNull String @NotNull [] args) {
        return this.rootCommand.suggestions(this.senderExtension.map(source.getSender()), new ArrayDeque<>(Arrays.asList(args)));
    }

    public @NotNull RootCommand<CommandSender, S> getRootCommand() {
        return this.rootCommand;
    }

    public List<String> getAliases() {
        return this.aliases;
    }

    public String getDescription() {
        return this.description;
    }

    public String getName() {
        return this.name;
    }
}