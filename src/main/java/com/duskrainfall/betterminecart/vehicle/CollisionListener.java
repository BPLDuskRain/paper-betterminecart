package com.duskrainfall.betterminecart.vehicle;

import org.bukkit.entity.*;
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
        if(minecart.hasGravity()){
//            getLogger().log(Level.INFO,"检测到碰撞" + e.getBlock().getType() + "在" + e.getBlock().getLocation());
            Vector vector = e.getVelocity();
            vector.setX(-vector.getX());
            vector.setZ(-vector.getZ());
            minecart.setVelocity(vector);
//            getLogger().log(Level.INFO,"速度改变");
        }
        else{
            Minecarts.stopFly(minecart);
            Minecarts.minecartExplosion(minecart);
        }
    }

    @EventHandler
    public void whenEntityCollision(VehicleEntityCollisionEvent e){
        if(!(e.getVehicle() instanceof Minecart minecart)) return;
        if(minecart.isEmpty()) return;
        if(!(minecart.getPassengers().get(0) instanceof Player)) return;
//        getLogger().log(Level.INFO, "检测到碰撞" + e.getEntity() + "在" + e.getEntity().getLocation());
//        getLogger().log(Level.INFO, "矿车速度变为" + minecart.getVelocity());

        Entity entity_crushed = e.getEntity();
        if(entity_crushed instanceof Minecart){
            Minecarts.minecartCrushed(minecart, (Minecart) entity_crushed);
        }
        else if(entity_crushed instanceof LivingEntity){
            Minecarts.livingEntityCrushed(minecart, (LivingEntity) entity_crushed);
            if(entity_crushed.getType() == EntityType.IRON_GOLEM){
                Minecarts.minecartExplosion(minecart);
            }
        }
        else {
            Minecarts.entityCrushed(minecart, entity_crushed);
        }
        e.setCancelled(true);
    }
}
