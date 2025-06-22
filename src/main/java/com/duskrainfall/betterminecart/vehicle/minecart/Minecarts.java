package com.duskrainfall.betterminecart.vehicle.minecart;

import com.duskrainfall.betterminecart.BetterMinecart;
import com.duskrainfall.betterminecart.vehicle.Vehicles;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.entity.minecart.RideableMinecart;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class Minecarts extends Vehicles {
    public final static double MAX = 3.2d;
    public final static double MAX_RAIL = 1.5d;
    public final static double MIN = 0.05d;
    public final static double LAND_MAX_Y = -0.2d;
    public final static double TO_FLY = 0.6d;
    public final static double TO_FALL = 0.4d;

    public final static int MAX_HEIGHT = 512;

    public final static float ANGLE_SIDE = 120.0f;
    public final static double CHANGE_SQUARE = 0.00015d;
    public final static double CHANGE_HEIGHT = 0.00006d;

    public final static float EAST = 90f;
    public final static float SOUTH = 180f;
    public final static float WEST = 270f;
    public final static float NORTH = 360f;

    public final static Vector flyingVelocityMod_ori = new Vector(0.95d, 0.95d, 0.95d);
    public final static Vector flyingVelocityMod_init = new Vector(0.995d, 1.0d, 0.995d);
    public final static Vector flyingVelocityMod_down = new Vector(0.995d, 1.001d, 0.995d);
    public final static Vector flyingVelocityMod_up = new Vector(0.995d, 0.99d, 0.995d);
    public final static Vector flyingVelocityMod_land = new Vector(0.99d, 0.8d, 0.99d);

    public final static Vector derailedVelocityMod = new Vector(0.98d, 0.98d, 0.98d);

    private final static int MAX_SPEED_CHANGE_CD = 10;

    private final static int MOVE_SOUND_CD = 20;
    public final static HashMap<Minecart, Integer> moveSoundCds = new HashMap<>();

    public final static int MAX_CAR_NUM = 8;
    public final static int MAX_CAR_LENGTH = 8;
    public final static ConcurrentHashMap<Entity, RideableMinecart> hookedMap = new ConcurrentHashMap<>();
    public final static ConcurrentHashMap<RideableMinecart, List<Entity>> cars = new ConcurrentHashMap<>();

//    public static void soundOnRail(RideableMinecart minecart, double speed){
//        if(moveSoundCds.containsKey(minecart)){
//            if(minecart.getTicksLived() - moveSoundCds.get(minecart) < MOVE_SOUND_CD) return;
//        }
//        minecart.getWorld().stopSound(SoundStop.named(Sound.ENTITY_MINECART_INSIDE));
//        if(!minecart.isEmpty()){
//            for(Entity entity : minecart.getNearbyEntities(5, 5, 5)){
//                if(entity instanceof Player player){
//                    player.playSound(
//                            minecart.getLocation(),
//                            Sound.ENTITY_MINECART_INSIDE,
//                            (float) Math.min(speed/2, 0.6), 1.0f
//                    );
//                }
//            }
//        }
//        moveSoundCds.put(minecart, minecart.getTicksLived());
//    }
    public static void soundNotOnRail(RideableMinecart minecart, double speed){
        if(moveSoundCds.containsKey(minecart)){
            if(minecart.getTicksLived() - moveSoundCds.get(minecart) < MOVE_SOUND_CD) return;
        }
        moveSoundCds.put(minecart, minecart.getTicksLived());

        if(!minecart.isEmpty()){
            for(Entity entity : minecart.getNearbyEntities(5, 5, 5)){
                if(entity instanceof Player player){
                    if(minecart.isInWater()){
                        player.playSound(
                                minecart.getLocation(),
                                Sound.ENTITY_AXOLOTL_SWIM,
                                (float) speed, 1.0f
                        );
                    }
                    else{
                        player.playSound(
                                minecart.getLocation(),
                                Sound.ENTITY_BREEZE_IDLE_GROUND,
                                (float) speed * 2, 0.8f
                        );
                    }
                }
            }
        }
    }
//    public static void soundOver(RideableMinecart minecart){
//        if(!minecart.isEmpty()){
//            for(Entity entity : minecart.getNearbyEntities(5, 5, 5)){
//                if(entity instanceof Player player){
//                    player.stopSound(SoundStop.named(Sound.ENTITY_MINECART_INSIDE));
//                    player.stopSound(SoundStop.named(Sound.ENTITY_AXOLOTL_SWIM));
//                    player.stopSound(SoundStop.named(Sound.ENTITY_BREEZE_IDLE_GROUND));
//                }
//            }
//        }
//    }

    private static void maxSpeedUpAnimation(Minecart minecart){
        minecart.getWorld().spawnParticle(
                Particle.FIREWORK, // 粒子类型
                minecart.getLocation().add(0, 1, 0), // 位置
                20, // 数量
                0.5, 0.5, 0.5, // 偏移量
                0.1 // 速度
        );
        minecart.getWorld().playSound(
                minecart.getLocation(),
                Sound.ENTITY_FIREWORK_ROCKET_LAUNCH,
                0.7f,
                1.0f
        );
        minecart.getWorld().playSound(
                minecart.getLocation(),
                Sound.ENTITY_BREEZE_JUMP,
                1.0f,
                1.0f
        );
    }
    public static void maxSpeedUp(RideableMinecart minecart, Player player){
        double maxSpeed = minecart.getMaxSpeed();
        if(maxSpeed == 0){
            player.sendActionBar(Component.text("正在停车，请解除制动或等待机车启动", NamedTextColor.RED));
        }
        else if(maxSpeed >= MAX) {
            minecart.setMaxSpeed(MAX);
            player.sendActionBar(Component.text("已经解放全部速度限制", NamedTextColor.RED));
        }
        else{
            if(Vehicles.controlCooling(player, "矿车速度解放", MAX_SPEED_CHANGE_CD)) return;

            minecart.setMaxSpeed(maxSpeed*=2);
            player.sendActionBar(Component.text("载具最大单向速率已经增加到 "
                    + maxSpeed + " block/tick", NamedTextColor.AQUA));
            maxSpeedUpAnimation(minecart);
        }
    }

    private static void maxSpeedDnAnimation(Minecart minecart){
        minecart.getWorld().spawnParticle(
                Particle.LAVA,
                minecart.getLocation().add(0, 1, 0),
                20,
                0.5, 0.5, 0.5,
                0.1
        );

        minecart.getWorld().playSound(
                minecart.getLocation(),
                Sound.ENTITY_BREEZE_SHOOT,
                1.0f,
                0.6f
        );
        minecart.getWorld().playSound(
                minecart.getLocation(),
                Sound.ENTITY_IRON_GOLEM_REPAIR,
                0.6f,
                1.0f
        );
        minecart.getWorld().playSound(
                minecart.getLocation(),
                Sound.BLOCK_RESPAWN_ANCHOR_DEPLETE,
                0.8f,
                1.0f
        );
    }
    public static void maxSpeedDn(RideableMinecart minecart, Player player){
        double maxSpeed = minecart.getMaxSpeed();
        if(maxSpeed == 0){
            player.sendActionBar(Component.text("正在停车，请解除制动或等待机车启动", NamedTextColor.RED));
        }
        else if(maxSpeed <= MIN) {
            minecart.setMaxSpeed(MIN);
            player.sendActionBar(Component.text("已经启用全部速度限制", NamedTextColor.RED));
        }
        else{
            if(Vehicles.controlCooling(player, "矿车速度限制", MAX_SPEED_CHANGE_CD)) return;

            minecart.setMaxSpeed(maxSpeed/=2);
            player.sendActionBar(Component.text("载具最大单向速率已经限制到 "
                    + maxSpeed + " block/tick", NamedTextColor.AQUA));
            maxSpeedDnAnimation(minecart);
        }
    }

    public static void back(RideableMinecart minecart){
        minecart.setVelocity(minecart.getVelocity().multiply(-1));
    }
    public static void reset(RideableMinecart minecart){
        minecart.setMaxSpeed(0.4);
    }
    public static void stop(RideableMinecart minecart, int depth){
        minecart.setVelocity(minecart.getVelocity().multiply(0));
        minecart.setMaxSpeed(0);

        if(cars.containsKey(minecart)){
            for(Entity entity : cars.get(minecart)){
                if(!(entity instanceof RideableMinecart minecartCar)) continue;
                if(depth < MAX_CAR_LENGTH){
                    stop(minecartCar, depth + 1);
                }
            }
        }
    }

    public static void tryStartFly(RideableMinecart minecart, double speed){
        //为了防止下坡飞出，本格和车下都需要铁轨判断
        switch(minecart.getLocation().getBlock().getType()){
            case Material.RAIL: case Material.DETECTOR_RAIL: case Material.POWERED_RAIL:
                Minecarts.stopFly(minecart);
                break;
            default:
                switch(minecart.getLocation().add(0, -1, 0).getBlock().getType()){
                    case Material.AIR: case Material.CAVE_AIR: case Material.VOID_AIR:
                        Minecarts.startFly(minecart, speed);
                        break;
                    case Material.RAIL: case Material.DETECTOR_RAIL: case Material.POWERED_RAIL:
                        Minecarts.stopFly(minecart);
                        break;
                    default:
                        Minecarts.landing(minecart, speed);
                }
        }
    }
    public static void startFly(RideableMinecart minecart, double speed){
        var delay = Math.round(10 / speed);
        new BukkitRunnable(){
            @Override
            public void run(){
                if(minecart.isEmpty()) return;
                if(!(minecart.getPassengers().get(0) instanceof Player)) return;

                minecart.setGravity(false);
                minecart.setFlyingVelocityMod(flyingVelocityMod_init);
            }
        }.runTaskLater(JavaPlugin.getPlugin(BetterMinecart.class), delay);
    }

    public static void tryStopFly(RideableMinecart minecart){
        //为防止侧滑爆炸，先检测本格
        switch(minecart.getLocation().getBlock().getType()){
            case Material.RAIL: case Material.DETECTOR_RAIL: case Material.POWERED_RAIL:
                Minecarts.stopFly(minecart);
                break;
            default:
                switch (minecart.getLocation().add(0, -1, 0).getBlock().getType()){
                    case Material.AIR: case Material.CAVE_AIR: case Material.VOID_AIR:
                        break;
                    case Material.RAIL: case Material.DETECTOR_RAIL: case Material.POWERED_RAIL:
                        Minecarts.stopFly(minecart);
                        break;
                    case Material.WATER:
                        Minecarts.stopFly(minecart);
                        new BukkitRunnable(){
                            @Override
                            public void run(){
                                Minecarts.vehicleExplosion(minecart);
                            }
                        }.runTaskLater(JavaPlugin.getPlugin(BetterMinecart.class), 5);
                        break;
                    default:
                        Minecarts.stopFly(minecart);
                        Minecarts.vehicleExplosion(minecart);
                }
        }
    }
    public static void stopFly(RideableMinecart minecart){
        minecart.setGravity(true);
        minecart.setFlyingVelocityMod(flyingVelocityMod_ori);
    }

    public static boolean tryLanding(RideableMinecart minecart, double speed, double speed_y){
        if(speed_y < LAND_MAX_Y) return false;
        //为防止侧滑爆炸，先检测本格
        return switch(minecart.getLocation().getBlock().getType()){
            case Material.RAIL, Material.DETECTOR_RAIL, Material.POWERED_RAIL -> {
                Minecarts.stopFly(minecart);
                yield true;
            }
            default -> {
                 switch (minecart.getLocation().add(0, -1, 0).getBlock().getType()) {
                    case Material.AIR, Material.CAVE_AIR, Material.VOID_AIR -> {
                        yield false;
                    }
                    case Material.RAIL, Material.DETECTOR_RAIL, Material.POWERED_RAIL -> {
                        Minecarts.stopFly(minecart);
                        yield true;
                    }
                    default -> {
                        Minecarts.landing(minecart, speed);
                        yield true;
                    }
                }
            }
        };
    }
    public static void landing(RideableMinecart minecart, double speed){
        minecart.setFallDistance(0);// 对于坐在矿车上摔落的情况，是按照矿车掉落高度计算

        minecart.setFlyingVelocityMod(flyingVelocityMod_land);
        if(speed > TO_FLY){
            Minecarts.startFly(minecart, speed);
        }
        else if(speed < TO_FALL){
            Minecarts.stopFly(minecart);
        }
    }

    public static void flyModChange(RideableMinecart minecart, double velocity_y){
        if(velocity_y >= 0){
            //上飞
            if(!minecart.getFlyingVelocityMod().equals(Minecarts.flyingVelocityMod_up)) {
                minecart.setFlyingVelocityMod(Minecarts.flyingVelocityMod_up);
            }
        }else{
            //下飞
            if(!minecart.getFlyingVelocityMod().equals(Minecarts.flyingVelocityMod_down)){
                minecart.setFlyingVelocityMod(Minecarts.flyingVelocityMod_down);
            }
        }
    }
    public static void speedControl(RideableMinecart minecart){
        if(!(minecart.getPassengers().get(0) instanceof Player player)) return;
        if(player.getInventory().getItemInMainHand().getType() != Minecarts.CONTROL_ITEM) return;

        var location = player.getLocation();
        double yaw = (location.getYaw() + 180) % 360; //偏航角 180南z+ 270西x- +-360北z- 90东x+
        double pitch = location.getPitch(); //俯仰角 正数俯角负数仰角

        var velocity = minecart.getVelocity();

        if(EAST - ANGLE_SIDE <= yaw && yaw <= EAST + ANGLE_SIDE){
            //x+
            velocity.setX(velocity.getX() + (ANGLE_SIDE - Math.abs(yaw - EAST)) * CHANGE_SQUARE);
        }
        if(SOUTH - ANGLE_SIDE <= yaw && yaw <= SOUTH + ANGLE_SIDE){
            //z+
            velocity.setZ(velocity.getZ() + (ANGLE_SIDE - Math.abs(yaw - SOUTH)) * CHANGE_SQUARE);
        }
        if(WEST - ANGLE_SIDE <= yaw && yaw <= WEST + ANGLE_SIDE){
            //x-
            velocity.setX(velocity.getX() - (ANGLE_SIDE - Math.abs(yaw - WEST)) * CHANGE_SQUARE);
        }
        if((NORTH - ANGLE_SIDE <= yaw && yaw <= NORTH) || (0 <= yaw && yaw <= 0 + ANGLE_SIDE)){
            //z-
            velocity.setZ(velocity.getZ() - (ANGLE_SIDE - Math.min(Math.abs(yaw - NORTH), Math.abs(yaw - 0))) * CHANGE_SQUARE);
        }

        velocity.setY(velocity.getY() - pitch * CHANGE_HEIGHT);//负数仰角加，正数俯角减

        minecart.setVelocity(velocity);
    }

    public static void vehicleExplosion(RideableMinecart minecart){
        if(cars.containsKey(minecart)){
            for(var car : cars.get(minecart)){
                car.setGravity(true);
            }
        }
        Vehicles.vehicleExplosion(minecart);
    }

    public static void entityCrushed(RideableMinecart minecart, Entity entity){
        if(entity.isInsideVehicle()) return;

        var velocity = minecart.getVelocity();
        var newVelocity = velocity.setY(Math.abs(velocity.getY()) + 1).multiply(2);
        if(Double.isFinite(newVelocity.getX()) && Double.isFinite(newVelocity.getZ()) && Double.isFinite(newVelocity.getY())){
            entity.setVelocity(newVelocity);
        }
    }

    private static void vehicleCrushedSound(RideableMinecart minecart){
        if(Vehicles.crushSoundCooling(minecart)) return;

        minecart.getWorld().playSound(
                minecart.getLocation(),
                Sound.ENTITY_BREEZE_DEFLECT,
                1.0f,
                1.0f
        );
        minecart.getWorld().playSound(
                minecart.getLocation(),
                Sound.ENTITY_BLAZE_HURT,
                0.6f,
                1.0f
        );
    }
    public static void minecartCrushed(RideableMinecart minecart, Minecart minecart_crushed){
        if(minecart_crushed.isInsideVehicle()) return;

        vehicleCrushedSound(minecart);

        if(minecart_crushed instanceof RideableMinecart){
            if(!minecart_crushed.isEmpty() && minecart_crushed.getPassengers().get(0) instanceof Player) {
                return;
            }
        }

        if(Vehicles.crushCooling(minecart_crushed)) return;

        var velocity = minecart.getVelocity();
        var newVelocity = velocity.multiply(2);
        if(Double.isFinite(newVelocity.getX()) && Double.isFinite(newVelocity.getZ()) && Double.isFinite(newVelocity.getY())){
            minecart_crushed.setMaxSpeed(
                    Math.min(
                            Math.max(
                                    minecart_crushed.getMaxSpeed(),
                                    2 * Math.max(
                                            Math.max(
                                                    Math.abs(velocity.getX()),
                                                    Math.abs(velocity.getZ())
                                            ),
                                            Math.abs(velocity.getY())
                                    )
                            ),
                            MAX * 2
                    )
            );

            minecart_crushed.setVelocity(newVelocity);
        }
    }
    public static void boatCrushed(RideableMinecart minecart, Boat boat){
        if(boat.isInsideVehicle()) return;

        vehicleCrushedSound(minecart);

        if(Vehicles.crushCooling(boat)) return;

        var velocity = minecart.getVelocity();
        var newVelocity = velocity.setY(Math.abs(velocity.getY()) + 1).multiply(2);
        if(Double.isFinite(newVelocity.getX()) && Double.isFinite(newVelocity.getZ()) && Double.isFinite(newVelocity.getY())){
            boat.setVelocity(newVelocity);
        }
    }

    private static void livingEntityCrushedSound(RideableMinecart minecart){
        if(Vehicles.crushSoundCooling(minecart)) return;

        minecart.getWorld().playSound(
                minecart.getLocation(),
                Sound.ENTITY_BREEZE_DEFLECT,
                1.0f,
                1.0f
        );
        minecart.getWorld().playSound(
                minecart.getLocation(),
                Sound.ENTITY_IRON_GOLEM_REPAIR,
                0.6f,
                1.0f
        );
    }
    public static void livingEntityCrushed(RideableMinecart minecart, LivingEntity livingEntity){
        if(livingEntity.isInsideVehicle()) return;

        livingEntityCrushedSound(minecart);

        if(Vehicles.crushCooling(livingEntity)) return;

        var velocity = minecart.getVelocity();
        if(livingEntity.getType() != EntityType.IRON_GOLEM){
            var newVelocity = velocity.setY(Math.abs(velocity.getY()) + 1).multiply(2);
            if(Double.isFinite(newVelocity.getX()) && Double.isFinite(newVelocity.getZ()) && Double.isFinite(newVelocity.getY())){
                livingEntity.setVelocity(newVelocity);
            }
        }
        Vehicles.imbalance(livingEntity);
    }
}
