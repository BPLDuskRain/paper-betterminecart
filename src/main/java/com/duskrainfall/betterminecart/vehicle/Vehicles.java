package com.duskrainfall.betterminecart.vehicle;

import com.duskrainfall.betterminecart.spring.Springs;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.*;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class Vehicles {
    public final static Material CONTROL_ITEM = Material.RECOVERY_COMPASS;

    public final static int LISTEN_GAP = 20;
    public final static ConcurrentHashMap<Vehicle, Integer> listenGapMap = new ConcurrentHashMap<>();

    public final static HashMap<Vehicle, BossBar> speedStateBar = new HashMap<>();

    private final static int CRUSHED_CD = 20;
    public final static ConcurrentHashMap<Entity, Integer> crushedCds = new ConcurrentHashMap<>();

    private final static int CRUSHED_SOUND_CD = 10;
    public final static ConcurrentHashMap<Vehicle, Integer> crushedSoundCds = new ConcurrentHashMap<>();

    private final static int CONTROL_CD = 10;
    public final static ConcurrentHashMap<Player, Integer> controlCds = new ConcurrentHashMap<>();

    public static double getSpeed(VehicleMoveEvent e){
        Location from = e.getFrom();
        Location to = e.getTo();
        double x = Math.abs(from.getX() - to.getX());
        double y = Math.abs(from.getY() - to.getY());
        double z = Math.abs(from.getZ() - to.getZ());
        return Math.sqrt(x*x+y*y+z*z);
    }
    public static double getSquaredSpeed(VehicleMoveEvent e){
        Location from = e.getFrom();
        Location to = e.getTo();
        double x = Math.abs(from.getX() - to.getX());
        double z = Math.abs(from.getZ() - to.getZ());
        return Math.sqrt(x*x+z*z);
    }
    public static Vector getVelocity(VehicleMoveEvent e){
        Location from = e.getFrom();
        Location to = e.getTo();
        double x = to.getX() - from.getX();
        double y = to.getY() - from.getY();
        double z = to.getZ() - from.getZ();
        return new Vector(x, y, z);
    }

    public static boolean controlCooling(Player player, String opName){
        if(controlCds.containsKey(player)){
            int ticks = player.getTicksLived() - controlCds.get(player);
            if(ticks < CONTROL_CD){
                player.sendActionBar(Component.text(opName + "还有 " + (CONTROL_CD - ticks) + " 刻冷却时间！", NamedTextColor.RED));
                return true;
            }
        }
        controlCds.put(player, player.getTicksLived());
        return false;
    }
    public static boolean crushCooling(Entity entity_crushed){
        if(crushedCds.containsKey(entity_crushed)){
            if(entity_crushed.getTicksLived() - crushedCds.get(entity_crushed) < CRUSHED_CD){
                crushedCds.put(entity_crushed, entity_crushed.getTicksLived());
                return true;
            }
        }
        crushedCds.put(entity_crushed, entity_crushed.getTicksLived());
        return false;
    }
    public static boolean crushSoundCooling(Vehicle vehicle){
        if(crushedSoundCds.containsKey(vehicle)){
            if(vehicle.getTicksLived() - crushedSoundCds.get(vehicle) < CRUSHED_SOUND_CD){
                crushedSoundCds.put(vehicle, vehicle.getTicksLived());
                return true;
            }
        }
        crushedSoundCds.put(vehicle, vehicle.getTicksLived());
        return false;
    }

    private static void vehicleExplosionAnimation(Vehicle vehicle){
        vehicle.getWorld().spawnParticle(Particle.EXPLOSION_EMITTER, vehicle.getLocation(),
                5, 1, 1, 1
        );
        vehicle.getWorld().playSound(
                vehicle.getLocation(),
                Sound.ENTITY_GENERIC_EXPLODE,
                1.0f, 1.0f
        );
    }
    public static void vehicleExplosion(Vehicle vehicle){
        for(Entity entity : vehicle.getPassengers()){
            if(entity instanceof Damageable eneity_damageable){
                var velocity = vehicle.getVelocity();
                double x = Math.abs(velocity.getX());
                double y = Math.abs(velocity.getY());
                double z = Math.abs(velocity.getZ());
                eneity_damageable.damage(10 * Math.max(Math.max(x, z), y));
                eneity_damageable.setFireTicks(120);
            }
        }
        vehicle.eject();
        vehicleExplosionAnimation(vehicle);
        Springs.createSpring(vehicle, 1200);
        vehicle.remove();
    }

    public static void imbalance(LivingEntity livingEntity){
        livingEntity.addPotionEffect(new PotionEffect(
                PotionEffectType.WEAKNESS,
                200, 3, false
        ));
        livingEntity.addPotionEffect(new PotionEffect(
                PotionEffectType.SLOWNESS,
                200, 3, false
        ));
        livingEntity.addPotionEffect(new PotionEffect(
                PotionEffectType.NAUSEA,
                200, 0, false
        ));
    }
}
