package com.duskrainfall.betterminecart;

import com.duskrainfall.betterminecart.spring.EditSpringListener;
import com.duskrainfall.betterminecart.vehicle.CollisionListener;
import com.duskrainfall.betterminecart.vehicle.SpeedControllerListener;
import com.duskrainfall.betterminecart.vehicle.VehicleBackCommand;
import com.duskrainfall.betterminecart.vehicle.VehicleSpeedListener;
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

        Bukkit.getPluginCommand("vehicleback").setExecutor(new VehicleBackCommand());
        PluginManager pluginManager =  Bukkit.getPluginManager();
        pluginManager.registerEvents(new VehicleSpeedListener(), this);
        pluginManager.registerEvents(new SpeedControllerListener(), this);
        pluginManager.registerEvents(new CollisionListener(), this);

        EditSpringListener editSpringListener =  new EditSpringListener(this);
        pluginManager.registerEvents(editSpringListener, this);
        editSpringListener.springEffect();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
//        System.out.println("BetterMinecart插件已经卸载！");
        getLogger().log(Level.INFO, "BetterMinecart插件已经卸载！");
    }
}
