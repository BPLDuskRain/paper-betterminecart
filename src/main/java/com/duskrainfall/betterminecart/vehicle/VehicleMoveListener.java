package com.duskrainfall.betterminecart.vehicle;

import com.duskrainfall.betterminecart.vehicle.minecart.Minecarts;
import io.papermc.paper.entity.LookAnchor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.entity.minecart.RideableMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.util.Vector;

public class VehicleMoveListener implements Listener {
    @EventHandler
    public void lock(VehicleMoveEvent e){
        Vehicle vehicle = e.getVehicle();
        if(vehicle.isEmpty()) return;

        if(!Vehicles.viewLock.containsKey(vehicle)) return;
        if(!Vehicles.viewLock.get(vehicle).isView()) return;

        Vector velocity = Vehicles.getVelocity(e);
        Location location = vehicle.getLocation();

        Vehicles.View view = Vehicles.viewLock.get(vehicle);
        var queue = view.getQueue();
        Vector sum = view.getSum();

        if(queue.size() == Vehicles.MAXSIZE) {
            sum.subtract(queue.poll());
        }
        sum.add(velocity);
        queue.offer(velocity);

        for(Entity entity : vehicle.getPassengers()){
            if(entity instanceof Player player){
                double x = location.getX() + sum.getX();
                double y = location.getY() + sum.getY();
                double z = location.getZ() + sum.getZ();
                player.lookAt(x, y, z, LookAnchor.FEET);
            }
        }

        if(vehicle instanceof RideableMinecart minecart){
            if(Math.round(sum.getX()) != 0 && Math.round(sum.getZ()) != 0){
                if(view.getSpeed() == 0.0d){
                    view.setSpeed(minecart.getMaxSpeed());
                    minecart.setMaxSpeed(Minecarts.OUT);
                }
            }
            else{
                if(view.getSpeed() != 0.0d){
                    minecart.setMaxSpeed(view.getSpeed());
                    view.setSpeed(0.0d);
                }
            }
        }
    }
}
