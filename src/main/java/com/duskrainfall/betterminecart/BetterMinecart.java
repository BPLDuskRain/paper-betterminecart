package com.duskrainfall.betterminecart;

import com.duskrainfall.betterminecart.spring.DropItemListener;
import com.duskrainfall.betterminecart.spring.Springs;
import com.duskrainfall.betterminecart.vehicle.*;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public final class BetterMinecart extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
//        System.out.println("BetterMinecart插件已经加载！");
        getLogger().log(Level.INFO, "BetterMinecart插件已经加载！");

        PluginCommand vehicleCommand = Bukkit.getPluginCommand("vehicle");
        vehicleCommand.setExecutor(new VehicleCommand());
        vehicleCommand.setTabCompleter(new VehicleTabCompleter());

        PluginManager pluginManager =  Bukkit.getPluginManager();
        pluginManager.registerEvents(new VehicleMovementListener(), this);
        pluginManager.registerEvents(new DriveListener(), this);
        pluginManager.registerEvents(new CollisionListener(), this);

        DropItemListener dropItemListener =  new DropItemListener();
        pluginManager.registerEvents(dropItemListener, this);

        Springs.springEffect();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
//        System.out.println("BetterMinecart插件已经卸载！");
        getLogger().log(Level.INFO, "BetterMinecart插件已经卸载！");
    }
}
