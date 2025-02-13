package com.ryderbelserion.example.commands.features;

import dev.triumphteam.cmd.core.annotations.Command;
import dev.triumphteam.cmd.core.annotations.Permission;
import dev.triumphteam.cmd.core.enums.Mode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HelpCommand extends BaseCommand {

    @Command("help")
    public void help(final Player player) {
        player.sendRichMessage("<red>This is a help message!");
    }

    @Command
    public void root(final Player player) {
        player.sendRichMessage("<red>This is a root message!");
    }

    @Command("console")
    @Permission(value = "console.use", def = Mode.OP)
    public void console(final CommandSender sender) {
        sender.sendRichMessage("<red>This is a console message!");
    }
}