package com.duskrainfall.betterminecart.vehicle.minecart;

import com.duskrainfall.betterminecart.vehicle.Vehicles;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.RideableMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.List;

public class MinecartMoveListener implements Listener {
    @EventHandler
    public void enterVehicle(VehicleEnterEvent e){
        if(!(e.getEntered() instanceof Player player)) return;
        if(!(e.getVehicle() instanceof RideableMinecart minecart)) return;

        if(!Vehicles.speedStateBar.containsKey(minecart)){
            BossBar bossBar = Bukkit.createBossBar("§e欢迎使用BetterMinecart！", BarColor.GREEN, BarStyle.SEGMENTED_20);
            Vehicles.speedStateBar.put(minecart, bossBar);
        }

        Vehicles.speedStateBar.get(minecart).addPlayer(player);
    }

    @EventHandler
    public void leaveVehicle_fly(VehicleExitEvent e){
        if(!(e.getVehicle() instanceof RideableMinecart minecart)) return;
        if(!(e.getExited() instanceof Player player)) return;

//        Minecarts.soundOver(minecart);

        if(minecart.hasGravity()) return;

        Minecarts.stopFly(minecart);
        player.addPotionEffect(new PotionEffect(
                PotionEffectType.SLOW_FALLING,
                200, 0, false
        ));
    }

    @EventHandler
    public void onMinecart_speed(VehicleMoveEvent e){
        if(!(e.getVehicle() instanceof RideableMinecart minecart)) return;
        if(minecart.isEmpty()) return;
        if(!(minecart.getPassengers().get(0) instanceof Player)) return;

        if(!Minecarts.listenGapMap.containsKey(minecart)){
            Minecarts.listenGapMap.put(minecart, 0);
        }
        else{
            int minecartCount = Minecarts.listenGapMap.get(minecart);
            if(minecartCount > 0){
                Minecarts.listenGapMap.put(minecart, minecartCount-1);
                return;
            }
            Minecarts.listenGapMap.put(minecart, minecart.hasGravity() ? Minecarts.LISTEN_GAP : Minecarts.LISTEN_GAP / 2);
        }

        double speed = Minecarts.getSpeed(e);
        double maxSpeed = minecart.getMaxSpeed();
        if(speed > Minecarts.MAX * 2) {
            Minecarts.vehicleExplosion(minecart);
            return;
        }
        double squaredSpeed = Minecarts.getSquaredSpeed(e);
        Vector velocity = Minecarts.getVelocity(e);

        //获取乘客
        List<Entity> passengers = minecart.getPassengers();
        for(Entity passenger : passengers) {
            if (!(passenger instanceof Player player)) {
                continue;
            }
            player.sendActionBar(Component.text("当前速度信息："
                    + String.format("%.2f", squaredSpeed) + ','
                    + String.format("%.2f", speed) + '('
                    + String.format("%.2f", velocity.getX()) + ' '
                    + String.format("%.2f", velocity.getY()) + ' '
                    + String.format("%.2f", velocity.getZ()) + ')'
                    + '/' + String.format("%.2f", maxSpeed)
                    + " block/tick"
                    , NamedTextColor.GREEN));
        }

        if(!Vehicles.speedStateBar.containsKey(minecart)) return;
        BossBar bossBar = Vehicles.speedStateBar.get(minecart);

        double height = minecart.getLocation().getY();
        double y = velocity.getY();
        //高度过高
        if(height > Minecarts.MAX_HEIGHT * 0.9){
            bossBar.setColor(BarColor.RED);
            bossBar.setProgress(Math.min(height / Minecarts.MAX_HEIGHT, 1));
            bossBar.setTitle("§c§l您即将失压 PULL DOWN!");

            minecart.getWorld().playSound(
                    minecart.getLocation(),
                    Sound.BLOCK_BELL_USE,
                    1.0f, 1.0f
            );
        }
        //下降率过大
        else if(y < Minecarts.LAND_MAX_Y){
            bossBar.setColor(BarColor.RED);
            bossBar.setProgress(Math.min(- Minecarts.LAND_MAX_Y - y , 1));
            bossBar.setTitle("§c§l下降率过大 PULL UP!");

            minecart.getWorld().playSound(
                    minecart.getLocation(),
                    Sound.BLOCK_BELL_USE,
                    1.0f, 1.0f
            );
        }
        //无重力
        else if(!minecart.hasGravity()){
            //无重力->即将失速
            if(Minecarts.TO_FALL <= speed && speed < Minecarts.TO_FALL + 0.2d){
                if (minecart.getFlyingVelocityMod().equals(Minecarts.flyingVelocityMod_land)) {
                    bossBar.setColor(BarColor.GREEN);
                    bossBar.setProgress(2 - (speed / Minecarts.TO_FALL));
                    bossBar.setTitle("§a您即将降落，请注意");

                    minecart.getWorld().playSound(
                            minecart.getLocation(),
                            Sound.ENTITY_BREEZE_SLIDE,
                            1.0f, 1.0f
                    );
                }
                else {
                    bossBar.setColor(BarColor.RED);
                    bossBar.setProgress(Math.min(1, speed / Minecarts.MAX));
                    bossBar.setTitle("§c§l您即将失速，请小心");

                    minecart.getWorld().playSound(
                            minecart.getLocation(),
                            Sound.BLOCK_BELL_USE,
                            1.0f, 0.8f
                    );
                }
            }
            //无重力->正常
            else{
                bossBar.setColor(BarColor.BLUE);
                bossBar.setProgress(Math.min(1, speed / Minecarts.MAX));
                bossBar.setTitle("§b顺风顺水，一切正常");
            }
        }
        else{
            //有重力
            bossBar.setColor(BarColor.GREEN);
            bossBar.setProgress(Math.min(1, speed / Minecarts.MAX_RAIL));
            bossBar.setTitle("§a祝道岔好，一切正常");
        }
    }

    @EventHandler
    public void onMinecart_fly(VehicleMoveEvent e) {
        if(!(e.getVehicle() instanceof RideableMinecart minecart)) return;
        if(minecart.isEmpty()) return;
        if(!(minecart.getPassengers().get(0) instanceof Player)) return;

        if(Minecarts.hookedMap.containsKey(minecart)) return;

        double speed = Minecarts.getSpeed(e);

        if(minecart.hasGravity()){
//            Minecarts.soundOnRail(minecart, speed);
            Minecarts.speedControl(minecart);
            minecart.setDerailedVelocityMod(Minecarts.derailedVelocityMod);
            if(speed > Minecarts.TO_FLY) Minecarts.tryStartFly(minecart, speed);
        }
        else {
            if(minecart.getLocation().getY() > Minecarts.MAX_HEIGHT){
                Minecarts.vehicleExplosion(minecart);
                return;
            }

            Minecarts.soundNotOnRail(minecart, speed);

            if (speed < Minecarts.TO_FALL) {
                Minecarts.stopFly(minecart);
                return;
            }

            double velocity_y = Minecarts.getVelocity(e).getY();

            if(!minecart.getFlyingVelocityMod().equals(Minecarts.flyingVelocityMod_land)){
                Minecarts.flyModChange(minecart, velocity_y);
            }
            else{
                Minecarts.tryStartFly(minecart, speed);
            }

            Minecarts.speedControl(minecart);
            if (!Minecarts.tryLanding(minecart, speed, velocity_y)) {
                Minecarts.tryStopFly(minecart);
            }
        }
    }

    @EventHandler
    public void OnHooked(VehicleMoveEvent e){
        if(!(e.getVehicle() instanceof RideableMinecart minecart)) return;

        if(!Minecarts.cars.containsKey(minecart)) return;

        boolean hasGravity = minecart.hasGravity();
        double headMaxSpeed = minecart.getMaxSpeed();
        Vector headVelocity = Minecarts.getVelocity(e);
        double length = headVelocity.length();
        Vector offset =  headVelocity.clone().multiply(length > 0.6 ? 1.2/length : 3);
        Location headLocation = minecart.getLocation().subtract(offset);
        for(Entity entity : Minecarts.cars.get(minecart)){
            if(!entity.isValid()) continue;

            entity.setGravity(hasGravity);
            entity.setFallDistance(minecart.getFallDistance());
            if(entity instanceof RideableMinecart hooking){
                hooking.setMaxSpeed(hasGravity ? headMaxSpeed + 0.1 : headMaxSpeed);
                hooking.setDerailedVelocityMod(Minecarts.derailedVelocityMod);
            }

            Location myLocation = entity.getLocation().add(offset);
            Vector vector_land = new Vector(
                    headLocation.x() - myLocation.x(),
                    headLocation.y() - myLocation.y(),
                    headLocation.z() - myLocation.z()
            );
            Vector vector_fly = vector_land.clone().multiply(0.5);
            entity.setVelocity(headVelocity.add(hasGravity ? vector_land : vector_fly));
        }
    }
}

