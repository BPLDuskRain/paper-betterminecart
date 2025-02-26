package com.duskrainfall.betterminecart.vehicle;

import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleBlockCollisionEvent;
import org.bukkit.event.vehicle.VehicleEntityCollisionEvent;
import org.bukkit.util.Vector;

public class CollisionListener implements Listener {
    @EventHandler
    public void whenBlockCollision(VehicleBlockCollisionEvent e){
        if(!(e.getVehicle() instanceof Minecart minecart)) return;
        if(minecart.isEmpty()) return;
        if(!(minecart.getPassengers().get(0) instanceof Player)) return;
//        getLogger().log(Level.INFO,"检测到碰撞" + e.getBlock().getType() + "在" + e.getBlock().getLocation());
        Vector vector = e.getVelocity();
        vector.setX(-vector.getX());
        vector.setZ(-vector.getZ());
        minecart.setVelocity(vector);
//        getLogger().log(Level.INFO,"速度改变");
    }

    @EventHandler
    public void whenEntityCollision(VehicleEntityCollisionEvent e){
        if(!(e.getVehicle() instanceof Minecart minecart)) return;
        if(minecart.isEmpty()) return;
        if(!(minecart.getPassengers().get(0) instanceof Player)) return;
//        getLogger().log(Level.INFO, "检测到碰撞" + e.getEntity() + "在" + e.getEntity().getLocation());
//        getLogger().log(Level.INFO, "矿车速度变为" + minecart.getVelocity());
        Vector vector = minecart.getVelocity();

        if(e.getEntity() instanceof Minecart minecart_attacked){
            minecart_attacked.setMaxSpeed(2 * Math.max(vector.getX(), vector.getZ()));
        }
        e.getEntity().setVelocity(vector.setY(Math.abs(vector.getY()) + 1).multiply(2));
        e.setCancelled(true);
    }
}
