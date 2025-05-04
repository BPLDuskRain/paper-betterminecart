package com.duskrainfall.betterminecart.vehicle;

import com.duskrainfall.betterminecart.vehicle.minecart.Minecarts;
import org.bukkit.entity.Entity;
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
    private static void removeHooked(Entity entity){
        if(Minecarts.hookedMap.containsKey(entity)){
            var hooked = Minecarts.hookedMap.get(entity);
            if(Minecarts.cars.containsKey(hooked)){
                Minecarts.cars.get(hooked).remove(entity);
            }
        }
    }

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
            Minecarts.cars.remove(minecart); // 本车被钩时整个移除
        }
        // 移除被钩车对本车的联系
        removeHooked(vehicle);
        // 移除钩子映射
        Minecarts.hookedMap.remove(vehicle);
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

        removeHooked(entity);
        Minecarts.hookedMap.remove(entity);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e){
        Vehicles.controlCds.remove(e.getPlayer());
    }
}
