package com.duskrainfall.betterminecart;

import com.duskrainfall.betterminecart.vehicle.*;
import com.duskrainfall.betterminecart.vehicle.CollisionListener;
import com.duskrainfall.betterminecart.vehicle.DriveListener;
import com.duskrainfall.betterminecart.vehicle.boat.BoatMoveListener;
import com.duskrainfall.betterminecart.vehicle.minecart.MinecartConnectListener;
import com.duskrainfall.betterminecart.vehicle.minecart.MinecartMoveListener;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class BetterMinecart extends JavaPlugin {
    @Override
    public void onEnable() {
        // Plugin startup logic

        PluginCommand vehicleCommand = Bukkit.getPluginCommand("vehicle");
        vehicleCommand.setExecutor(new VehicleCommand());
        vehicleCommand.setTabCompleter(new VehicleTabCompleter());

        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new MinecartMoveListener(), this);
        pluginManager.registerEvents(new MinecartConnectListener(), this);
        pluginManager.registerEvents(new BoatMoveListener(), this);
        pluginManager.registerEvents(new DriveListener(), this);
        pluginManager.registerEvents(new CollisionListener(), this);
        pluginManager.registerEvents(new KillEntityListener(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
