package com.duskrainfall.betterminecart;

import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleBlockCollisionEvent;
import org.bukkit.util.Vector;

import java.util.logging.Level;

import static org.bukkit.Bukkit.getLogger;

public class CollisionListener implements Listener {
    @EventHandler
    public void whenCollision(VehicleBlockCollisionEvent e){
        if(!(e.getVehicle() instanceof Minecart minecart)) return;
        if(minecart.isEmpty()) return;
        if(!(minecart.getPassengers().get(0) instanceof Player)) return;
//        getLogger().log(Level.INFO,"检测到碰撞" + e.getBlock().getType() + "在" + e.getBlock().getLocation());
        Vector vector =  e.getVelocity();
        vector.setX(-vector.getX());
        vector.setZ(-vector.getZ());
        minecart.setVelocity(vector);
//        getLogger().log(Level.INFO,"速度改变");
    }
}
