package com.duskrainfall.betterminecart.vehicle;

import com.duskrainfall.betterminecart.vehicle.minecart.Minecarts;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
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
        AfterKilling.afterDestroyVehicle(vehicle);
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
        Entity entity = e.getEntity();
        Vehicles.crushedCds.remove(entity);

        // 移除车头对本实体的联系
        AfterKilling.removeHooked(entity);
        // 移除本实体对车头的联系
        Minecarts.toHead.remove(entity);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e){
        Vehicles.controlCds.remove(e.getPlayer());
    }
}
