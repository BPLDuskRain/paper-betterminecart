package com.duskrainfall.betterminecart.vehicle.boat;

import com.duskrainfall.betterminecart.vehicle.Vehicles;
import org.bukkit.Sound;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;

import java.util.HashMap;

public class Boats extends Vehicles {
    private final static int CRUSHED_SOUND_CD = 10;
    public final static HashMap<Boat, Integer> crushedSoundCds = new HashMap<>();

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
        vehicleCrushedSound(boat);
        vehicle_crushed.setVelocity(vehicle_crushed.getVelocity().add(boat.getVelocity()));
    }

    private static void playerCrushedSound(Boat boat){
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
    }
    synchronized public static void playerCrushed(Boat boat, Player player){
        playerCrushedSound(boat);
        player.setVelocity(player.getVelocity().add(boat.getVelocity().multiply(2)));
    }
}
