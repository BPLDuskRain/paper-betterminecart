package com.duskrainfall.betterminecart.vehicle.boat;

import com.duskrainfall.betterminecart.vehicle.Vehicles;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Boat;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.util.Vector;

import java.util.HashMap;

public class Boats extends Vehicles {
    private final static int CRUSHED_SOUND_CD = 10;
    public final static HashMap<Boat, Integer> crushedSoundCds = new HashMap<>();

    public static void boatUp(Boat boat, Player player){
        if(boat.isOnGround()){
            player.sendActionBar(Component.text("地上无法起跳", NamedTextColor.RED));
        }
        else{
            switch(boat.getLocation().add(0, -1, 0).getBlock().getType()){
                case Material.AIR: case Material.CAVE_AIR:
                    player.sendActionBar(Component.text("空中无法起跳", NamedTextColor.RED));
                    break;
                default:
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
        if(crushedSoundCds.containsKey(boat)){
            if(boat.getTicksLived() - crushedSoundCds.get(boat) < CRUSHED_SOUND_CD){
                crushedSoundCds.put(boat, boat.getTicksLived());
                return;
            }
        }

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

        crushedSoundCds.put(boat, boat.getTicksLived());
    }
    synchronized public static void vehicleCrushed(Boat boat, Vehicle vehicle_crushed){
        if(vehicle_crushed.isInsideVehicle()) return;

        vehicleCrushedSound(boat);

        if(!vehicle_crushed.isEmpty() && vehicle_crushed.getPassengers().get(0) instanceof Player){
            return;
        }

        if(crushedCds.containsKey(vehicle_crushed)){
            if(vehicle_crushed.getTicksLived() - crushedCds.get(vehicle_crushed) < CRUSHED_CD){
                crushedCds.put(vehicle_crushed, vehicle_crushed.getTicksLived());
                return;
            }
        }

        Vector velocity = boat.getVelocity();
        vehicle_crushed.setVelocity(
                velocity.multiply(2)
                .add(vehicle_crushed.getVelocity().multiply(-1))
                .setY(Math.abs(velocity.getY()) + 1)
        );

        crushedCds.put(vehicle_crushed, vehicle_crushed.getTicksLived());
    }

    private static void livingEntityCrushedSound(Boat boat){
        if(crushedSoundCds.containsKey(boat)){
            if(boat.getTicksLived() - crushedSoundCds.get(boat) < CRUSHED_SOUND_CD) {
                crushedSoundCds.put(boat, boat.getTicksLived());
                return;
            }
        }

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

        crushedSoundCds.put(boat, boat.getTicksLived());
    }
    synchronized public static void livingEntityCrushed(Boat boat, LivingEntity livingEntity){
        if(livingEntity.isInsideVehicle()) return;

        livingEntityCrushedSound(boat);

        if(crushedCds.containsKey(livingEntity)){
            if(livingEntity.getTicksLived() - crushedCds.get(livingEntity) < CRUSHED_CD){
                crushedCds.put(livingEntity, livingEntity.getTicksLived());
                return;
            }
        }

        Vector velocity = boat.getVelocity();
        livingEntity.setVelocity(
                velocity.multiply(2)
                .add(livingEntity.getVelocity().multiply(-1))
                .setY(Math.abs(velocity.getY()) + 1)
        );
        Vehicles.imbalance(livingEntity);

        crushedCds.put(livingEntity, livingEntity.getTicksLived());
    }
}
