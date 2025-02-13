package com.ryderbelserion.example.commands.features;

import com.ryderbelserion.example.ExamplePlugin;
import dev.triumphteam.cmd.core.annotations.Command;
import org.bukkit.plugin.java.JavaPlugin;

@Command("ember")
public abstract class BaseCommand {

    protected ExamplePlugin plugin = JavaPlugin.getPlugin(ExamplePlugin.class);

}