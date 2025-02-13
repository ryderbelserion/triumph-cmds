package com.ryderbelserion.example;

import com.ryderbelserion.example.commands.CommandManager;
import org.bukkit.plugin.java.JavaPlugin;

public class ExamplePlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        CommandManager.load();
    }

    @Override
    public void onDisable() {

    }
}