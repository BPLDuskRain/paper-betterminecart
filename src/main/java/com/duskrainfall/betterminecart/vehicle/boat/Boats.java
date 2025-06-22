package com.duskrainfall.betterminecart.vehicle.boat;

import com.duskrainfall.betterminecart.vehicle.Vehicles;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Boat;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.util.Vector;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class Boats extends Vehicles {
    public final static double MAX = 3.2d;
    public final static double FIRE = 1.0d;
    public final static int FREEZE_TICK = 36;
    public final static int EXPLODE_TICK = 384;
    private final static int MAX_MAGNET_FACTOR = 4;
    public final static int LISTEN_GAP = 10;

    private final static int FLOAT_CHANGE_CD = 10;
    private final static int MAGNET_CHANGE_CD = 10;
    private final static int JUMP_CD = 10;

    private final static int JUMP_HEIGHT = 1;
    private final static double FLOAT_FACTOR = 0.05d;

    private final static double MOVE_RADIUS = 0.4d;
    private final static double MOVE_RADIUS_Y = 0.2d;
    public final static ConcurrentHashMap<Boat, Boolean> boatFloatMap = new ConcurrentHashMap<>();
    public final static ConcurrentHashMap<Boat, Integer> boatMagnetMap = new ConcurrentHashMap<>();

    public static void drive(Boat boat, Player player){
        if(Vehicles.controlCooling(player, "磁力悬浮切换", FLOAT_CHANGE_CD)) return;

        player.sendActionBar(Component.text("正在调整磁力状态", NamedTextColor.BLUE));
        Boats.floatSwitch(boat);
    }
    private static void floatSwitch(Boat boat){
        boolean floating = Optional.ofNullable(Boats.boatFloatMap.get(boat))
                .map(v -> !v)
                .orElse(false);
        Boats.boatFloatMap.put(boat, floating);

        boat.getWorld().playSound(
                boat.getLocation(),
                Sound.ENTITY_BREEZE_INHALE,
                1.0f, 1.0f
        );

        if(!Vehicles.speedStateBar.containsKey(boat)) return;
        BossBar bossBar = Vehicles.speedStateBar.get(boat);
        if(floating){
            bossBar.setColor(BarColor.BLUE);
            bossBar.setTitle("§b磁场稳定，一切正常");
        }
        else{
            bossBar.setColor(BarColor.GREEN);
            bossBar.setTitle("§a风平浪静，正常航行");
            Boats.boatMagnetMap.put(boat, 0);
        }
    }

    public static void move(Boat boat, Player player){
        boolean floating = Optional.ofNullable(Boats.boatFloatMap.get(boat)).orElse(false);
        if(!floating){
            switch(boat.getStatus()){
                case Boat.Status.ON_LAND -> {
                    player.sendActionBar(Component.text("地上无法起跳", NamedTextColor.RED));
                }
                case Boat.Status.IN_AIR->{
                    player.sendActionBar(Component.text("空中无法起跳", NamedTextColor.RED));
                }
                default -> {
                    if(Vehicles.controlCooling(player, "立体机动装置", JUMP_CD)) return;

                    player.sendActionBar(Component.text("起跳！", NamedTextColor.GREEN));
                    Boats.jump(boat);
                }
            }
        }
        else{
            if(Vehicles.controlCooling(player, "磁力加速调整", MAGNET_CHANGE_CD)) return;

            player.sendActionBar(Component.text("正在调整磁力状态", NamedTextColor.BLUE));
            Boats.magnetSwitch(boat);
        }
    }
    private static void magnetSwitch(Boat boat){
        int magnet_factor = boat.isVisualFire() ? Optional.ofNullable(Boats.boatMagnetMap.get(boat))
                .map(v -> (v/2) % MAX_MAGNET_FACTOR)
                .orElse(0)
                : Optional.ofNullable(Boats.boatMagnetMap.get(boat))
                .map(v -> (v+1) % MAX_MAGNET_FACTOR)
                .orElse(0);
        Boats.boatMagnetMap.put(boat, magnet_factor);

        if(!Vehicles.speedStateBar.containsKey(boat)) return;
        BossBar bossBar = Vehicles.speedStateBar.get(boat);
        if(magnet_factor == 0){
            bossBar.setColor(BarColor.BLUE);
            bossBar.setTitle("§b磁场稳定，一切正常");
        }
        else{
            bossBar.setColor(BarColor.PURPLE);
            bossBar.setTitle("§b磁悬浮加速：启用中");
        }

        boat.getWorld().playSound(
                boat.getLocation(),
                Sound.BLOCK_RESPAWN_ANCHOR_CHARGE,
                1.0f, 0.8f
        );
        boat.getWorld().playSound(
                boat.getLocation(),
                Sound.ENTITY_ENDERMAN_TELEPORT,
                1.0f, 0.8f
        );
    }
    private static void jump(Boat boat){
        Vector velocity = boat.getVelocity();
        double y = velocity.getY() + JUMP_HEIGHT;
        if(Double.isFinite(y)){
            boat.setVelocity(velocity.setY(y));
        }
        boat.getWorld().playSound(
                boat.getLocation(),
                Sound.ENTITY_GENERIC_SPLASH,
                1.0f, 1.0f
        );
    }

    public static void floating(Boat boat){
        if(boat.getStatus() == Boat.Status.IN_WATER) return;

        Vector velocity = boat.getVelocity();

        double y = velocity.getY() + FLOAT_FACTOR;

        if(Double.isFinite(y)){
            boat.setVelocity(velocity.setY(y));
        }
    }
    public static void magnet(Boat boat, Player player){
        Vector velocity = boat.getVelocity();

        var location = player.getLocation();
        double yaw = (location.getYaw() + 90) % 270; //偏航角 90南z+ 180西x- +-270北z- 0东x+
        double yaw_radius = yaw * Math.PI / 180;
        double pitch = location.getPitch(); //俯仰角 正数俯角负数仰角
        double pitch_radius = -pitch * Math.PI / 180;

        int magnet_factor = Optional.ofNullable(Boats.boatMagnetMap.get(boat)).orElse(0);

        double x = Math.cos(yaw_radius) * MOVE_RADIUS * magnet_factor;
        double z = Math.sin(yaw_radius) * MOVE_RADIUS * magnet_factor;
        double y = boat.getStatus() == Boat.Status.IN_WATER ? 0.0d
                : Math.sin(pitch_radius) * MOVE_RADIUS_Y * magnet_factor + FLOAT_FACTOR;

        if(Double.isFinite(x) && Double.isFinite(z) && Double.isFinite(y)){
            boat.setVelocity(velocity.setX(x).setZ(z).setY(y));
        }
    }

    private static void vehicleCrushedSound(Boat boat){
        if(Vehicles.crushSoundCooling(boat)) return;

        boat.getWorld().playSound(
                boat.getLocation(),
                Sound.ENTITY_BREEZE_DEFLECT,
                1.0f,
                1.0f
        );
        boat.getWorld().playSound(
                boat.getLocation(),
                Sound.ENTITY_BLAZE_HURT,
                0.6f,
                1.0f
        );
    }
    public static void vehicleCrushed(Boat boat, Vehicle vehicle_crushed){
        if(vehicle_crushed.isInsideVehicle()) return;

        vehicleCrushedSound(boat);

        if(!vehicle_crushed.isEmpty() && vehicle_crushed.getPassengers().get(0) instanceof Player){
            return;
        }

        if(Vehicles.crushCooling(vehicle_crushed)) return;

        Vector velocity = boat.getVelocity();
        vehicle_crushed.setVelocity(
                velocity.multiply(2)
                        .add(vehicle_crushed.getVelocity().multiply(-1))
                        .setY(Math.abs(velocity.getY()) + 1)
        );
    }

    private static void livingEntityCrushedSound(Boat boat){
        if(Vehicles.crushSoundCooling(boat)) return;

        boat.getWorld().playSound(
                boat.getLocation(),
                Sound.ENTITY_BREEZE_DEFLECT,
                1.0f,
                1.0f
        );
        boat.getWorld().playSound(
                boat.getLocation(),
                Sound.ENTITY_IRON_GOLEM_REPAIR,
                0.6f,
                1.0f
        );
    }
    public static void livingEntityCrushed(Boat boat, LivingEntity livingEntity){
        if(livingEntity.isInsideVehicle()) return;

        livingEntityCrushedSound(boat);

        if(Vehicles.crushCooling(livingEntity)) return;

        Vector velocity = boat.getVelocity();
        livingEntity.setVelocity(
                velocity.multiply(2)
                        .add(livingEntity.getVelocity().multiply(-1))
                        .setY(Math.abs(velocity.getY()) + 1)
        );
        Vehicles.imbalance(livingEntity);
    }
}
