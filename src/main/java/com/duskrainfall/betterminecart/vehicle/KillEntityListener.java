package com.duskrainfall.betterminecart.vehicle;

import com.duskrainfall.betterminecart.vehicle.minecart.Minecarts;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.entity.minecart.RideableMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;

public class KillEntityListener implements Listener {
    @EventHandler
    public void destroyVehicle(VehicleDestroyEvent e){
        Vehicle vehicle = e.getVehicle();
        Vehicles.crushedCds.remove(vehicle);
        Vehicles.crushedSoundCds.remove(vehicle);
        if(Vehicles.speedStateBar.containsKey(vehicle)){
            Vehicles.speedStateBar.get(vehicle).removeAll();
            Vehicles.speedStateBar.remove(vehicle);
        }
        if(vehicle instanceof RideableMinecart minecart){
//            Minecarts.soundOver(minecart);
            Minecarts.moveSoundCds.remove(minecart);
        }
    }

    @EventHandler
    public void leaveVehicle(VehicleExitEvent e){
        if(!(e.getExited() instanceof Player player)) return;
        Vehicle vehicle = e.getVehicle();

        if(Vehicles.speedStateBar.containsKey(vehicle)){
            var bar = Vehicles.speedStateBar.get(vehicle);
            if(bar.getPlayers().contains(player)){
                bar.removePlayer(player);
            }
        }
    }

    @EventHandler
    public void entityDie(EntityDeathEvent e){
        Vehicles.crushedCds.remove(e.getEntity());
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e){
        Vehicles.controlCds.remove(e.getPlayer());
    }
}
