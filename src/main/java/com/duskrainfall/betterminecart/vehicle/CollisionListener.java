package com.duskrainfall.betterminecart.vehicle;

import com.duskrainfall.betterminecart.vehicle.boat.Boats;
import com.duskrainfall.betterminecart.vehicle.minecart.Minecarts;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.entity.minecart.RideableMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleBlockCollisionEvent;
import org.bukkit.event.vehicle.VehicleEntityCollisionEvent;
import org.bukkit.util.Vector;

public class CollisionListener implements Listener {
    @EventHandler
    public void whenBlockCollision(VehicleBlockCollisionEvent e){
        Entity vehicleEntity = e.getVehicle();
        if(vehicleEntity.isEmpty()) return;
        if(!(vehicleEntity.getPassengers().get(0) instanceof Player)) return;

        switch(e.getBlock().getType()){
            case Material.RAIL, Material.DETECTOR_RAIL, Material.POWERED_RAIL:
                return;
            default:
                break;
        }
        if(vehicleEntity instanceof RideableMinecart minecart){
            if(minecart.hasGravity()){
                Vector vector = e.getVelocity();
                vector.setX(-vector.getX());
                vector.setZ(-vector.getZ());
                minecart.setVelocity(vector);
            }
            else{
                Minecarts.stopFly(minecart);
                Minecarts.vehicleExplosion(minecart);
            }
        }
        else if(vehicleEntity instanceof Boat boat){

        }
    }

    @EventHandler
    public void whenEntityCollision(VehicleEntityCollisionEvent e){
        Entity vehicleEntity = e.getVehicle();
        if(vehicleEntity.isEmpty()) return;
        if(!(vehicleEntity.getPassengers().get(0) instanceof Player)) return;

        if(vehicleEntity instanceof RideableMinecart minecart){
            Entity entity_crushed = e.getEntity();
            if(entity_crushed instanceof Minecart){
                Minecarts.minecartCrushed(minecart, (Minecart) entity_crushed);
            }
            else if(entity_crushed instanceof LivingEntity){
                Minecarts.livingEntityCrushed(minecart, (LivingEntity) entity_crushed);
                if(entity_crushed.getType() == EntityType.IRON_GOLEM){
                    Minecarts.vehicleExplosion(minecart);
                }
            }
            else {
                Minecarts.entityCrushed(minecart, entity_crushed);
            }
            e.setCancelled(true);
        }
        else if(vehicleEntity instanceof Boat boat){
            Entity entity_crushed = e.getEntity();
            if(entity_crushed instanceof Player){
                Boats.playerCrushed(boat, (Player) entity_crushed);
            }
            else if(entity_crushed instanceof Vehicle){
                Boats.vehicleCrushed(boat, (Vehicle) entity_crushed);
            }
        }
    }
}
