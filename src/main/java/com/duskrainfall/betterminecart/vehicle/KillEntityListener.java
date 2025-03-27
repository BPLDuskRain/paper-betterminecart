package com.duskrainfall.betterminecart.vehicle;

import com.duskrainfall.betterminecart.vehicle.boat.Boats;
import com.duskrainfall.betterminecart.vehicle.minecart.Minecarts;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Vehicle;
import org.bukkit.entity.minecart.RideableMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;

public class KillEntityListener implements Listener {
    @EventHandler
    public void destroyVehicle(VehicleDestroyEvent e){
        Vehicle vehicle = e.getVehicle();
        Vehicles.crushedCds.remove(vehicle);
        if((vehicle instanceof RideableMinecart minecart)){
            Minecarts.soundOver(minecart);
            Minecarts.moveSoundCds.remove(minecart);
            Minecarts.crushedSoundCds.remove(minecart);
        }
        if(vehicle instanceof Boat boat){
            Boats.crushedSoundCds.remove(boat);
        }
    }

    @EventHandler
    public void entityDie(EntityDeathEvent e){
        Vehicles.crushedCds.remove(e.getEntity());
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e){
        DriveListener.cds.remove(e.getPlayer());
    }
}
