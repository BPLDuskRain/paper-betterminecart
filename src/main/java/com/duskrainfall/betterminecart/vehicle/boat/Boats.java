package com.duskrainfall.betterminecart.vehicle.boat;

import com.duskrainfall.betterminecart.vehicle.Vehicles;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Sound;
import org.bukkit.entity.Boat;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.util.Vector;

public class Boats extends Vehicles {
    public static void jump(Boat boat, Player player){
        switch(boat.getStatus()){
            case Boat.Status.ON_LAND -> {
                player.sendActionBar(Component.text("地上无法起跳", NamedTextColor.RED));
            }
            case Boat.Status.IN_AIR -> {
                player.sendActionBar(Component.text("空中无法起跳", NamedTextColor.RED));
            }
            default -> {
                if(Vehicles.controlCooling(player, "立体机动装置")) return;

                Vector velocity = boat.getVelocity();
                boat.setVelocity(velocity.setY(velocity.getY() + 1));
                boat.getWorld().playSound(
                        boat.getLocation(),
                        Sound.ENTITY_GENERIC_SPLASH,
                        1.0f, 1.0f
                );
            }
        }
    }

    private static void vehicleCrushedSound(Boat boat){
        if(Vehicles.crushSoundCooling(boat)) return;

        boat.getWorld().playSound(
                boat.getLocation(),
                Sound.ENTITY_BREEZE_DEFLECT,
                1.0f,
                1.0f
        );
        boat.getWorld().playSound(
                boat.getLocation(),
                Sound.ENTITY_BLAZE_HURT,
                0.6f,
                1.0f
        );
    }
    public static void vehicleCrushed(Boat boat, Vehicle vehicle_crushed){
        if(vehicle_crushed.isInsideVehicle()) return;

        vehicleCrushedSound(boat);

        if(!vehicle_crushed.isEmpty() && vehicle_crushed.getPassengers().get(0) instanceof Player){
            return;
        }

        if(Vehicles.crushCooling(vehicle_crushed)) return;

        Vector velocity = boat.getVelocity();
        vehicle_crushed.setVelocity(
                velocity.multiply(2)
                        .add(vehicle_crushed.getVelocity().multiply(-1))
                        .setY(Math.abs(velocity.getY()) + 1)
        );
    }

    private static void livingEntityCrushedSound(Boat boat){
        if(Vehicles.crushSoundCooling(boat)) return;

        boat.getWorld().playSound(
                boat.getLocation(),
                Sound.ENTITY_BREEZE_DEFLECT,
                1.0f,
                1.0f
        );
        boat.getWorld().playSound(
                boat.getLocation(),
                Sound.ENTITY_IRON_GOLEM_REPAIR,
                0.6f,
                1.0f
        );
    }
    public static void livingEntityCrushed(Boat boat, LivingEntity livingEntity){
        if(livingEntity.isInsideVehicle()) return;

        livingEntityCrushedSound(boat);

        if(Vehicles.crushCooling(livingEntity)) return;

        Vector velocity = boat.getVelocity();
        livingEntity.setVelocity(
                velocity.multiply(2)
                        .add(livingEntity.getVelocity().multiply(-1))
                        .setY(Math.abs(velocity.getY()) + 1)
        );
        Vehicles.imbalance(livingEntity);
    }
}
