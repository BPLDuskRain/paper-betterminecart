package com.duskrainfall.betterminecart.vehicle;

import com.duskrainfall.betterminecart.BetterMinecart;
import com.duskrainfall.betterminecart.spring.Springs;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.entity.minecart.RideableMinecart;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;

public class Minecarts {
    public final static Material CONTROL_ITEM = Material.RECOVERY_COMPASS;

    public final static int LISTEN_GAP = 20;

    public final static double MAX = 1.6d;
    public final static double MAX_RAIL = 1.5d;
    public final static double MIN = 0.05d;
    public final static double LAND_MAX_Y = -0.2d;
    public final static double TO_FLY = 0.6d;
    public final static double TO_FALL = 0.2d;

    public final static float ANGLE_SQUARE = 90.0f;
    public final static double CHANGE_SQUARE = 0.0001d;
    public final static double CHANGE_HEIGHT = 0.00005d;

    public final static float EAST = 90f;
    public final static float SOUTH = 180f;
    public final static float WEST = 270f;
    public final static float NORTH = 360f;

    public final static Vector flyingVelocityMod_ori = new Vector(0.95d, 0.95d, 0.95d);
    public final static Vector flyingVelocityMod_init = new Vector(0.995d, 1.0d, 0.995d);
    public final static Vector flyingVelocityMod_down = new Vector(0.995d, 1.001d, 0.995d);
    public final static Vector flyingVelocityMod_up = new Vector(0.995d, 0.99d, 0.995d);
    public final static Vector flyingVelocityMod_land = new Vector(0.99d, 0.8d, 0.99d);

    public final static Vector derailedVelocityMod = new Vector(0.9d, 0.9d, 0.9d);

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
    public static void maxSpeedUpEvent(Minecart minecart, Player player){
        if(minecart.getMaxSpeed() >= MAX) {
            minecart.setMaxSpeed(MAX);
            player.sendActionBar(Component.text("已经解放全部速度限制", NamedTextColor.RED));
        }
        else{
            minecart.setMaxSpeed(minecart.getMaxSpeed()*2);
            if(minecart.hasGravity()){
                player.sendActionBar(Component.text("载具最大单向速率已经增加到 "
                        + Math.min(minecart.getMaxSpeed(), MAX_RAIL)
                        + " block/tick", NamedTextColor.AQUA));
            }
            else{
                player.sendActionBar(Component.text("载具最大单向速率已经增加到 "
                        + minecart.getMaxSpeed()
                        + " block/tick", NamedTextColor.AQUA));
            }
            maxSpeedUpAnimation(minecart);
        }
    }

    private static void maxSpeedDnAnimation(Minecart minecart){
        minecart.getWorld().spawnParticle(
                Particle.LAVA, // 粒子类型
                minecart.getLocation().add(0, 1, 0), // 位置
                20, // 数量
                0.5, 0.5, 0.5, // 偏移量
                0.1 // 速度
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
    }
    public static void maxSpeedDnEvent(Minecart minecart, Player player){
        if(minecart.getMaxSpeed() <= MIN) {
            minecart.setMaxSpeed(MIN);
            player.sendActionBar(Component.text("已经启用全部速度限制", NamedTextColor.RED));
        }
        else{
            minecart.setMaxSpeed(minecart.getMaxSpeed()/2);
            player.sendActionBar(Component.text("载具最大单向速率已经限制到 "
                    + minecart.getMaxSpeed() + " block/tick", NamedTextColor.AQUA));
            maxSpeedDnAnimation(minecart);
        }
    }

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

    public static void tryStartFly(Minecart minecart, double speed){
        //为了防止下坡飞出，本格和车下都需要铁轨判断
        switch(minecart.getLocation().getBlock().getType()){
            case Material.RAIL: case Material.DETECTOR_RAIL: case Material.POWERED_RAIL:
                break;
            default:
                switch(minecart.getLocation().add(0, -1, 0).getBlock().getType()){
                    case Material.AIR: case Material.CAVE_AIR:
                        Minecarts.startFly(minecart, speed);
                        break;
                    case Material.RAIL: case Material.DETECTOR_RAIL: case Material.POWERED_RAIL:
                        break;
                    default:
                        Minecarts.landing(minecart, speed);
                }
        }
    }
    public static void startFly(Minecart minecart, double speed){
        var delay = Math.round(10 / speed);
        new BukkitRunnable(){
            @Override
            public void run(){
                minecart.setGravity(false);
                minecart.setFlyingVelocityMod(flyingVelocityMod_init);
            }
        }.runTaskLater(JavaPlugin.getPlugin(BetterMinecart.class), delay);
    }

    public static void tryStopFly(Minecart minecart){
        switch (minecart.getLocation().add(0, -1, 0).getBlock().getType()){
            case Material.AIR: case Material.CAVE_AIR:
                break;
            case Material.RAIL: case Material.DETECTOR_RAIL: case Material.POWERED_RAIL:
                Minecarts.stopFly(minecart);
                break;
            case Material.WATER:
                Minecarts.stopFly(minecart);
                new BukkitRunnable(){
                    @Override
                    public void run(){
                        Minecarts.minecartExplosion(minecart);
                    }
                }.runTaskLater(JavaPlugin.getPlugin(BetterMinecart.class), 5);
                break;
            default:
                Minecarts.stopFly(minecart);
                Minecarts.minecartExplosion(minecart);
        }
    }
    public static void stopFly(Minecart minecart){
        minecart.setGravity(true);
        minecart.setFlyingVelocityMod(flyingVelocityMod_ori);
    }

    public static boolean tryLanding(Minecart minecart, double speed, double speed_y){
        if(speed_y < LAND_MAX_Y) return false;
        return switch (minecart.getLocation().add(0, -1, 0).getBlock().getType()) {
            case Material.AIR, Material.CAVE_AIR -> false;
            case Material.RAIL, Material.DETECTOR_RAIL, Material.POWERED_RAIL -> {
                Minecarts.stopFly(minecart);
                yield false;
            }
            default -> {
                Minecarts.landing(minecart, speed);
                yield true;
            }
        };
    }
    public static void landing(Minecart minecart, double speed){
        minecart.setFallDistance(0);// 对于坐在矿车上摔落的情况，是按照矿车掉落高度计算
        //minecart.getPassengers().get(0).setFallDistance(0);
        //加一点延迟 避免先于可能的碰撞触发导致碰撞不触发坠机
        new BukkitRunnable(){
            @Override
            public void run(){
                minecart.setFlyingVelocityMod(flyingVelocityMod_land);
                if(speed > TO_FLY){
                    Minecarts.startFly(minecart, speed);
                }
                else if(speed < TO_FALL){
                    Minecarts.stopFly(minecart);
                }
            }
        }.runTaskLater(JavaPlugin.getPlugin(BetterMinecart.class), 2);
    }

    public static void flyControl(Minecart minecart){
        if(!(minecart.getPassengers().get(0) instanceof Player player)) return;
        if(player.getInventory().getItemInMainHand().getType() != Minecarts.CONTROL_ITEM) return;

        var location = player.getLocation();
        var yaw = (location.getYaw() + 180) % 360; //偏航角 180南z+ 270西x- +-360北z- 90东x+
        var pitch = location.getPitch(); //俯仰角 正数俯角负数仰角

        var velocity = minecart.getVelocity();

        if(EAST - ANGLE_SQUARE <= yaw && yaw <= EAST + ANGLE_SQUARE){
            //x+
            velocity.setX(velocity.getX() + (ANGLE_SQUARE - Math.abs(yaw - EAST)) * CHANGE_SQUARE);
        }
        if(SOUTH - ANGLE_SQUARE <= yaw && yaw <= SOUTH + ANGLE_SQUARE){
            //z+
            velocity.setZ(velocity.getZ() + (ANGLE_SQUARE - Math.abs(yaw - SOUTH)) * CHANGE_SQUARE);
        }
        if(WEST - ANGLE_SQUARE <= yaw && yaw <= WEST + ANGLE_SQUARE){
            //x-
            velocity.setX(velocity.getX() - (ANGLE_SQUARE - Math.abs(yaw - WEST)) * CHANGE_SQUARE);
        }
        if((NORTH - ANGLE_SQUARE <= yaw && yaw <= NORTH) || (0 <= yaw && yaw <= 0 + ANGLE_SQUARE)){
            //z-
            velocity.setZ(velocity.getZ() - (ANGLE_SQUARE - Math.min(Math.abs(yaw - NORTH), Math.abs(yaw - 0))) * CHANGE_SQUARE);
        }

        velocity.setY(velocity.getY() - pitch * CHANGE_HEIGHT);//负数仰角加，正数俯角减

        minecart.setVelocity(velocity);
    }

    private static void minecartExplosionAnimation(Minecart minecart){
        minecart.getWorld().spawnParticle(Particle.EXPLOSION, minecart.getLocation(),
                50, 1, 1, 1
        );
        minecart.getWorld().playSound(
                minecart.getLocation(),
                Sound.ENTITY_GENERIC_EXPLODE,
                1.0f, 1.0f
        );
    }
    public static void minecartExplosion(Minecart minecart){
            for(Entity entity : minecart.getPassengers()){
                if(entity instanceof Damageable eneity_damageable){
                    var velocity = minecart.getVelocity();
                    double x = Math.abs(velocity.getX());
                    double y = Math.abs(velocity.getY());
                    double z = Math.abs(velocity.getZ());
                    eneity_damageable.damage(10 * Math.max(Math.max(x, z), y));
                    eneity_damageable.setFireTicks(120);
                }
            }
            minecart.eject();
            minecartExplosionAnimation(minecart);
            Springs.createSpring(minecart, 1200);
            minecart.remove();
    }

    private final static long SOUND_CD = 1000L;
    public final static HashMap<Minecart, Long> soundCds = new HashMap<>();
    public static void entityCrushed(RideableMinecart minecart, Entity entity){
        var velocity = minecart.getVelocity();
        entity.setVelocity(velocity.setY(Math.abs(velocity.getY()) + 1).multiply(2));
    }
    private static void minecartCrushedSound(RideableMinecart minecart){
        if(soundCds.containsKey(minecart)){
            if(System.currentTimeMillis() - soundCds.get(minecart) < SOUND_CD){
                soundCds.put(minecart,System.currentTimeMillis());
                return;
            }
        }
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
        soundCds.put(minecart,System.currentTimeMillis());
    }
    public static void minecartCrushed(RideableMinecart minecart, Minecart minecart_crushed){
        minecartCrushedSound(minecart);
        var velocity = minecart.getVelocity();
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
        if(minecart_crushed instanceof RideableMinecart){
            if(minecart_crushed.isEmpty() || !(minecart_crushed.getPassengers().get(0) instanceof Player)) {
                minecart_crushed.setVelocity(velocity.multiply(2));
            }
        }
    }
    private static void livingEntityCrushedSound(RideableMinecart minecart){
        if(soundCds.containsKey(minecart)){
            if(System.currentTimeMillis() - soundCds.get(minecart) < SOUND_CD) {
                soundCds.put(minecart,System.currentTimeMillis());
                return;
            }
        }
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
        livingEntityCrushedSound(minecart);
        var velocity = minecart.getVelocity();
        if(livingEntity.getType() != EntityType.IRON_GOLEM){
            livingEntity.setVelocity(velocity.setY(Math.abs(velocity.getY()) + 1).multiply(2));
        }
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
