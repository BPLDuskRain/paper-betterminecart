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

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.ConcurrentHashMap;

public class Vehicles {
    public final static Material CONTROL_ITEM = Material.RECOVERY_COMPASS;

    public final static int LISTEN_GAP = 20;
    public final static ConcurrentHashMap<Vehicle, Integer> listenGapMap = new ConcurrentHashMap<>();

    public final static ConcurrentHashMap<Vehicle, BossBar> speedStateBar = new ConcurrentHashMap<>();

    private final static int CRUSHED_CD = 20;
    public final static ConcurrentHashMap<Entity, Integer> crushedCds = new ConcurrentHashMap<>();

    private final static int CRUSHED_SOUND_CD = 10;
    public final static ConcurrentHashMap<Vehicle, Integer> crushedSoundCds = new ConcurrentHashMap<>();

    public final static ConcurrentHashMap<Player, Integer> controlCds = new ConcurrentHashMap<>();

    public final static ConcurrentHashMap<Vehicle, View> viewLock = new ConcurrentHashMap<>();
    public final static int MAXSIZE = 40;

    public static class View{
        private boolean view;
        private final Vector sum = new Vector(0, 0, 0);
        private final Deque<Vector> queue = new ArrayDeque<>(MAXSIZE);
        private double speed;

        public View(boolean view) {
            this.view = view;
        }

        public boolean isView() {
            return view;
        }
        public void setView(boolean view) {
            this.view = view;
        }
        public void notView(){
            this.view = !this.view;
        }

        public Vector getSum(){
            return sum;
        }
        public Deque<Vector> getQueue() {
            return queue;
        }

        public double getSpeed() {
            return speed;
        }
        public void setSpeed(double speed) {
            this.speed = speed;
        }
    }

    public static double getSpeed(VehicleMoveEvent e){
        Location from = e.getFrom();
        Location to = e.getTo();
        double x = to.getX() - from.getX();
        double y = to.getY() - from.getY();
        double z = to.getZ() - from.getZ();
        return Math.sqrt(x*x+y*y+z*z);
    }
    public static double getSquaredSpeed(VehicleMoveEvent e){
        Location from = e.getFrom();
        Location to = e.getTo();
        double x = to.getX() - from.getX();
        double z = to.getZ() - from.getZ();
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

    public static boolean controlCooling(Player player, String opName, int cd){
        if(controlCds.containsKey(player)){
            int ticks = player.getTicksLived() - controlCds.get(player);
            if(ticks < cd){
                player.sendActionBar(Component.text(opName + "还有 " + (cd - ticks) + " 刻冷却时间！", NamedTextColor.RED));
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

    public static void rainbowTail(Vehicle vehicle, double speed){
        var world = vehicle.getWorld();
        var location = vehicle.getLocation();
        int count = (int) Math.round(speed);
        world.spawnParticle(Particle.RAID_OMEN, location, count, 0.5, 0.1, 0.5);
        world.spawnParticle(Particle.TRIAL_OMEN, location, count, 0.5, 0.1, 0.5);
        location.add(0, 0.5, 0);
        world.spawnParticle(Particle.GLOW, location, count, 0.5, 0.5, 0.5);
        world.spawnParticle(Particle.WAX_ON, location, count, 0.5, 0.5, 0.5);
        world.spawnParticle(Particle.WAX_OFF, location, count, 0.5, 0.5, 0.5);
        world.spawnParticle(Particle.HAPPY_VILLAGER, location, count, 0.5, 0.5, 0.5);
    }

    public static void lock(Vehicle vehicle){
        if(!viewLock.containsKey(vehicle)){
            viewLock.put(vehicle, new View(true));
        }else {
            viewLock.get(vehicle).notView();
        }
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
        var velocity = vehicle.getVelocity();
        for(Entity entity : vehicle.getPassengers()){
            if(entity instanceof Damageable eneity_damageable){
                double x = Math.abs(velocity.getX());
                double y = Math.abs(velocity.getY());
                double z = Math.abs(velocity.getZ());
                eneity_damageable.damage(10 * Math.max(Math.max(x, z), y));
                eneity_damageable.setFireTicks(120);
            }
            entity.setVelocity(velocity);
        }
        vehicle.eject();
        vehicleExplosionAnimation(vehicle);
        Springs.createSpring(vehicle, 1200);
        vehicle.remove();
        AfterKilling.afterDestroyVehicle(vehicle);
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
