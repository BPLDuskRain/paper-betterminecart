package com.duskrainfall.betterminecart;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public final class BetterMinecart extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
//        System.out.println("BetterMinecart插件已经加载！");
        getLogger().log(Level.INFO, "BetterMinecart插件已经加载！");

//        Bukkit.getPluginCommand("vehiclespeed").setExecutor(new VehicleSpeed());
        PluginManager pluginManager =  Bukkit.getPluginManager();
        pluginManager.registerEvents(new VehicleSpeed(), this);
        pluginManager.registerEvents(new SpeedControllerListener(), this);
        pluginManager.registerEvents(new CollisionListener(), this);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
//        System.out.println("BetterMinecart插件已经卸载！");
        getLogger().log(Level.INFO, "BetterMinecart插件已经卸载！");
    }
}
