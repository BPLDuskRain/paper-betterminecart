package com.duskrainfall.betterminecart.vehicle;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class VehicleCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String nickname, @NotNull String[] args) {
        if(!(sender instanceof Player executor)){
            return true;
        }
        if(!executor.isInsideVehicle()){
            sender.sendMessage(executor.getName() + "不在载具中");
            return true;
        }
        if(!executor.hasGravity()){
            sender.sendMessage(executor.getName() + "滑翔/飞行中不可用！");
            return true;
        }
        Entity vehicle = executor.getVehicle();
        switch(args[0]){
            case "back": case"b":
                if(vehicle instanceof Minecart || vehicle instanceof Boat){
                    vehicle.setVelocity(vehicle.getVelocity().multiply(-1));
                    sender.sendMessage("§a两极反转！");
                }
                break;
            case "stop": case "s":
                if(vehicle instanceof Minecart || vehicle instanceof Boat){
                    vehicle.setVelocity(vehicle.getVelocity().multiply(0));
                    sender.sendMessage("§c寸止挑战！");
                }
                break;
        }

        return true;
    }
}
