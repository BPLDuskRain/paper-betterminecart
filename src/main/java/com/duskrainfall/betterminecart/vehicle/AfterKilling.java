package com.duskrainfall.betterminecart.vehicle;

import com.duskrainfall.betterminecart.vehicle.boat.Boats;
import com.duskrainfall.betterminecart.vehicle.minecart.Minecarts;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Vehicle;
import org.bukkit.entity.minecart.RideableMinecart;

public class AfterKilling {
    public static void removeHooked(Entity entity){
        if(Minecarts.toHead.containsKey(entity)){
            var hooked = Minecarts.toHead.get(entity);
            if(Minecarts.toCars.containsKey(hooked)){
                Minecarts.toCars.get(hooked).remove(entity);
            }
        }
    }

    public static void afterDestroyVehicle(Vehicle vehicle){
        Vehicles.listenGapMap.remove(vehicle);
        Vehicles.crushedCds.remove(vehicle);
        Vehicles.crushedSoundCds.remove(vehicle);
        if(Vehicles.speedStateBar.containsKey(vehicle)){
            Vehicles.speedStateBar.get(vehicle).removeAll();
            Vehicles.speedStateBar.remove(vehicle);
        }
        if(vehicle instanceof RideableMinecart minecart){
//            Minecarts.soundOver(minecart);
            Minecarts.moveSoundCds.remove(minecart);

            if(Minecarts.toCars.containsKey(minecart)){
                for(Entity entity : Minecarts.toCars.get(minecart)){
                    Minecarts.toHead.remove(entity);// 移除车厢对本车的联系
                }
                Minecarts.toCars.remove(minecart); // 移除本车对车厢的联系
            }
        }
        // 移除车头对本载具的联系
        removeHooked(vehicle);
        // 移除本载具对车头的联系
        Minecarts.toHead.remove(vehicle);

        if(vehicle instanceof Boat boat){
            Boats.boatFloatMap.remove(boat);
            Boats.boatMagnetMap.remove(boat);
        }
    }
}
