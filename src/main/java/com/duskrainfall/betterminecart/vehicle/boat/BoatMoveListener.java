package com.duskrainfall.betterminecart.vehicle.boat;

import com.duskrainfall.betterminecart.vehicle.Vehicles;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Optional;

public class BoatMoveListener implements Listener {
    @EventHandler
    public void enterVehicle(VehicleEnterEvent e){
        if(!(e.getEntered() instanceof Player player)) return;
        if(!(e.getVehicle() instanceof Boat boat)) return;

        if(!Boats.boatFloatMap.containsKey(boat)) Boats.boatFloatMap.put(boat, false);
        if(!Boats.boatMagnetMap.containsKey(boat)) Boats.boatMagnetMap.put(boat, 0);

        if(!Vehicles.speedStateBar.containsKey(boat)){
            BossBar bossBar = Bukkit.createBossBar("§a风平浪静，正常航行", BarColor.GREEN, BarStyle.SEGMENTED_20);
            Vehicles.speedStateBar.put(boat, bossBar);
        }

        Vehicles.speedStateBar.get(boat).addPlayer(player);
    }

    @EventHandler
    public void onBoat_speed(VehicleMoveEvent e){
        if(!(e.getVehicle() instanceof Boat boat)) return;
        if(boat.isEmpty()) return;
        if(!(boat.getPassengers().get(0) instanceof Player)) return;

        if(!Boats.listenGapMap.containsKey(boat)){
            Boats.listenGapMap.put(boat, 0);
        }
        else{
            int boatCount = Boats.listenGapMap.get(boat);
            if(boatCount > 0){
                Boats.listenGapMap.put(boat, boatCount-1);
                return;
            }
            Boats.listenGapMap.put(boat, Boats.LISTEN_GAP);
        }

        double speed = Boats.getSpeed(e);
        double squaredSpeed = Boats.getSquaredSpeed(e);
        Vector velocity = Boats.getVelocity(e);

        //获取乘客
        List<Entity> passengers = boat.getPassengers();
        for(Entity passenger : passengers){
            if(!(passenger instanceof Player player)){
                continue;
            }
            player.sendActionBar(Component.text("当前速度信息："
                            + String.format("%.2f", squaredSpeed) + ','
                            + String.format("%.2f", speed) + '('
                            + String.format("%.2f", velocity.getX()) + ' '
                            + String.format("%.2f", velocity.getY()) + ' '
                            + String.format("%.2f", velocity.getZ()) + ')'
                            + " block/tick"
                    , NamedTextColor.GREEN));

            if(speed > Boats.FIRE){
                player.setFreezeTicks(player.getFreezeTicks() + Boats.FREEZE_TICK);
                player.sendActionBar(Component.text("热过载：" + player.getFreezeTicks()
                                + '/' + Boats.EXPLODE_TICK + ' '
                                + "当前速度信息："
                                + String.format("%.2f", squaredSpeed) + ','
                                + String.format("%.2f", speed) + '('
                                + String.format("%.2f", velocity.getX()) + ' '
                                + String.format("%.2f", velocity.getY()) + ' '
                                + String.format("%.2f", velocity.getZ()) + ')'
                                + " block/tick"
                        , NamedTextColor.RED));
            }
            if(player.getFreezeTicks() > Boats.EXPLODE_TICK){
                Boats.vehicleExplosion(boat);
                return;
            }
        }

        boat.setVisualFire(speed > Boats.FIRE);

        if(!Vehicles.speedStateBar.containsKey(boat)) return;
        BossBar bossBar = Vehicles.speedStateBar.get(boat);
        bossBar.setProgress(Math.min(1, speed / Boats.MAX));
    }

    @EventHandler
    public void onBoat_magnet(VehicleMoveEvent e){
        if(!(e.getVehicle() instanceof Boat boat)) return;
        if(boat.isEmpty()) return;
        if(!(boat.getPassengers().get(0) instanceof Player player)) return;

        boolean floating = Optional.ofNullable(Boats.boatFloatMap.get(boat)).orElse(false);
        int magnet_factor = Optional.ofNullable(Boats.boatMagnetMap.get(boat)).orElse(0);
        if(floating){
            if(magnet_factor == 0) Boats.floating(boat);
            else Boats.magnet(boat, player);
        }
    }
}
