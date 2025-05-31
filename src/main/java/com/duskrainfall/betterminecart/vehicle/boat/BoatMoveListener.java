package com.duskrainfall.betterminecart.vehicle.boat;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.util.Vector;

import java.util.List;

public class BoatMoveListener implements Listener {
    int boatCount = 0;
    @EventHandler
    public void onBoat_speed(VehicleMoveEvent e){
        if(!(e.getVehicle() instanceof Boat boat)) return;
        if(boat.isEmpty()) return;
        if(!(boat.getPassengers().get(0) instanceof Player)) return;

        if(boatCount > 0){
            boatCount--;
            return;
        }
        boatCount = Boats.LISTEN_GAP;

        double speed = Boats.getSpeed(e);
        double squaredSpeed = Boats.getSquaredSpeed(e);
        Vector velocity = Boats.getVelocity(e);

        //获取乘客
        List<Entity> passengers = boat.getPassengers();
        for(Entity passenger : passengers){
            if(!(passenger instanceof Player player)){
                continue;
            }
            player.sendActionBar(Component.text("当前速度信息："
                            + String.format("%.2f", squaredSpeed) + '/'
                            + String.format("%.2f", speed) + '('
                            + String.format("%.2f", velocity.getX()) + ' '
                            + String.format("%.2f", velocity.getY()) + ' '
                            + String.format("%.2f", velocity.getZ()) + ')'
                            + " block/tick"
                    , NamedTextColor.GREEN));
        }
    }
}
