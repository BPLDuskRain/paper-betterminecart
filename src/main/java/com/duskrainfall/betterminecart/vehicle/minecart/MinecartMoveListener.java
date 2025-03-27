package com.duskrainfall.betterminecart.vehicle.minecart;

import com.duskrainfall.betterminecart.BetterMinecart;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.RideableMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.List;

public class MinecartMoveListener implements Listener {
    @EventHandler
    public void leaveVehicle(VehicleExitEvent e){
        if(!(e.getVehicle() instanceof RideableMinecart minecart)) return;
        if(!(e.getExited() instanceof Player player)) return;

        Minecarts.soundOver(minecart);

        if(minecart.hasGravity()) return;

        Minecarts.stopFly(minecart);
        player.addPotionEffect(new PotionEffect(
                PotionEffectType.SLOW_FALLING,
                200, 0, false
        ));
    }

    int minecartCount = 0;
    @EventHandler
    public void onMinecart_speed(VehicleMoveEvent e){
        if(!(e.getVehicle() instanceof RideableMinecart minecart)) return;
        if(minecart.isEmpty()) return;
        if(!(minecart.getPassengers().get(0) instanceof Player)) return;

        if(minecartCount > 0){
            minecartCount--;
            return;
        }
        minecartCount = minecart.hasGravity() ? Minecarts.LISTEN_GAP : Minecarts.LISTEN_GAP / 2;

        double speed = Minecarts.getSpeed(e);
        double squaredSpeed = Minecarts.getSquaredSpeed(e);
        Vector velocity = Minecarts.getVelocity(e);
        if(speed > Minecarts.MAX * 2) {
            Minecarts.vehicleExplosion(minecart);
        }

        //获取乘客
        List<Entity> passengers = minecart.getPassengers();
        for(Entity passenger : passengers){
            if(!(passenger instanceof Player player)){
                continue;
            }
            player.sendActionBar(Component.text("当前平面速率/速率为 "
                    + String.format("%.2f", squaredSpeed) + '/'
                    + String.format("%.2f", speed) + '('
                    + String.format("%.2f", velocity.getX()) + ' '
                    + String.format("%.2f", velocity.getY()) + ' '
                    + String.format("%.2f", velocity.getZ()) + ')'
                    + " block/tick"
                    , NamedTextColor.GREEN));
            if(!minecart.hasGravity() && squaredSpeed < Minecarts.TO_FALL + 0.2d){
                if(minecart.getFlyingVelocityMod().equals(Minecarts.flyingVelocityMod_land)){
                    new BukkitRunnable(){
                        @Override
                        public void run(){
                            player.sendActionBar(Component.text("您即将降落，请注意", NamedTextColor.AQUA));
                            player.getWorld().playSound(
                                    player.getLocation(),
                                    Sound.ENTITY_BREEZE_SLIDE,
                                    1.0f, 1.0f
                            );
                        }
                    }.runTaskLater(JavaPlugin.getPlugin(BetterMinecart.class), Minecarts.LISTEN_GAP / 4);
                }
                else{
                    new BukkitRunnable(){
                        @Override
                        public void run(){
                            player.sendActionBar(Component.text("您即将失速 PULL UP!", NamedTextColor.DARK_RED));
                            player.getWorld().playSound(
                                    player.getLocation(),
                                    Sound.BLOCK_BELL_USE,
                                    1.0f, 1.0f
                            );
                        }
                    }.runTaskLater(JavaPlugin.getPlugin(BetterMinecart.class), Minecarts.LISTEN_GAP / 4);
                }
            }
        }
    }

    @EventHandler
    public void onMinecart_fly(VehicleMoveEvent e) {
        if(!(e.getVehicle() instanceof RideableMinecart minecart)) return;
        if(minecart.isEmpty()) return;
        if(!(minecart.getPassengers().get(0) instanceof Player)) return;

        if(!minecart.getDerailedVelocityMod().equals(Minecarts.derailedVelocityMod)){
            minecart.setDerailedVelocityMod(Minecarts.derailedVelocityMod);
        }

        double speed = Minecarts.getSpeed(e);

        if(minecart.hasGravity()){
//            Minecarts.soundOnRail(minecart, speed);
            if(speed > Minecarts.TO_FLY) Minecarts.tryStartFly(minecart, speed);
        }
        else {
            Minecarts.soundNotOnRail(minecart, speed);
            if (speed < Minecarts.TO_FALL) {
                Minecarts.stopFly(minecart);
            }

            double velocity_y = Minecarts.getVelocity(e).getY();

            if(!minecart.getFlyingVelocityMod().equals(Minecarts.flyingVelocityMod_land)){
                Minecarts.flyModChange(minecart, velocity_y);
            }
            else{
                Minecarts.tryStartFly(minecart, speed);
            }

            Minecarts.flyControl(minecart);
            if (!Minecarts.tryLanding(minecart, speed, velocity_y)) {
                Minecarts.tryStopFly(minecart);
            }
        }
    }
}

