package com.duskrainfall.betterminecart.vehicle;

import com.duskrainfall.betterminecart.spring.Springs;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.util.Vector;

public class Vehicles {
    public final static Material CONTROL_ITEM = Material.RECOVERY_COMPASS;

    public final static int LISTEN_GAP = 20;

    public static double getSpeed(VehicleMoveEvent e){
        Location from = e.getFrom();
        Location to = e.getTo();
        double x = Math.abs(from.getX() - to.getX());
        double y = Math.abs(from.getY() - to.getY());
        double z = Math.abs(from.getZ() - to.getZ());
        return Math.sqrt(x*x+y*y+z*z);
    }
    public static double getSquaredSpeed(VehicleMoveEvent e){
        Location from = e.getFrom();
        Location to = e.getTo();
        double x = Math.abs(from.getX() - to.getX());
        double z = Math.abs(from.getZ() - to.getZ());
        return Math.sqrt(x*x+z*z);
    }
    public static Vector getVelocity(VehicleMoveEvent e){
        Location from = e.getFrom();
        Location to = e.getTo();
        double x = to.getX() - from.getX();
        double y = to.getY() - from.getY();
        double z = to.getZ() - from.getZ();
        return new Vector(x, y, z);
    }

    private static void vehicleExplosionAnimation(Vehicle vehicle){
        vehicle.getWorld().spawnParticle(Particle.EXPLOSION, vehicle.getLocation(),
                50, 1, 1, 1
        );
        vehicle.getWorld().playSound(
                vehicle.getLocation(),
                Sound.ENTITY_GENERIC_EXPLODE,
                1.0f, 1.0f
        );
    }
    public static void vehicleExplosion(Vehicle vehicle){
        for(Entity entity : vehicle.getPassengers()){
            if(entity instanceof Damageable eneity_damageable){
                var velocity = vehicle.getVelocity();
                double x = Math.abs(velocity.getX());
                double y = Math.abs(velocity.getY());
                double z = Math.abs(velocity.getZ());
                eneity_damageable.damage(10 * Math.max(Math.max(x, z), y));
                eneity_damageable.setFireTicks(120);
            }
        }
        vehicle.eject();
        vehicleExplosionAnimation(vehicle);
        Springs.createSpring(vehicle, 1200);
        vehicle.remove();
    }
}
