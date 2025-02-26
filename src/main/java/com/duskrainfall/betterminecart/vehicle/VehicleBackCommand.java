package com.duskrainfall.betterminecart.vehicle;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class VehicleBackCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if(!(sender instanceof Player executor)){
            return true;
        }
        if(!executor.isInsideVehicle()){
            sender.sendMessage(executor.getName() + "不在载具中");
            return true;
        }
        Entity vehicle = executor.getVehicle();
        if(vehicle instanceof Minecart || vehicle instanceof Boat){
            vehicle.setVelocity(vehicle.getVelocity().multiply(-1));
            sender.sendMessage("§a两极反转！");
        }
        return true;
    }
}
