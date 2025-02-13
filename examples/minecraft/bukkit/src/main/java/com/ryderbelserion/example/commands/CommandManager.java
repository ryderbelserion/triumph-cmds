package com.ryderbelserion.example.commands;

import com.ryderbelserion.example.ExamplePlugin;
import com.ryderbelserion.example.commands.features.HelpCommand;
import dev.triumphteam.cmd.bukkit.BukkitCommandManager;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.List;

public class CommandManager {

    private static final ExamplePlugin plugin = JavaPlugin.getPlugin(ExamplePlugin.class);

    private static final BukkitCommandManager<CommandSender> commandManager = BukkitCommandManager.create(plugin);

    public static void load() {
        List.of(
                new HelpCommand()
        ).forEach(commandManager::registerCommand);
    }
}