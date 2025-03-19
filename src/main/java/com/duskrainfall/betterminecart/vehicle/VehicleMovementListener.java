package com.duskrainfall.betterminecart.vehicle;

import com.duskrainfall.betterminecart.BetterMinecart;
import net.kyori.adventure.sound.SoundStop;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.entity.minecart.RideableMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.List;

//class PlayerInfo{
//    public boolean commandUsed = false;
//    public boolean isOnVehicle = false;
//}

public class VehicleMovementListener implements Listener {
//    final private HashMap<String, PlayerInfo> infos = new HashMap<>();

//    @Override
//    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
//        //仅玩家使用该指令
//        if(!(commandSender instanceof Player player)){
//            commandSender.sendMessage("仅开放计算玩家速度");
//            return false;
//        }
//        //尝试注册hashmap
//        String name = player.getName();
//        if(!infos.containsKey(name)){
//            infos.put(name, new PlayerInfo());
//            System.out.println("玩家" + name + "成功注册");
//        }
//
//        infos.get(name).commandUsed = !infos.get(name).commandUsed;
//        player.sendMessage("已经将载具速度显示切换为 " + infos.get(name).commandUsed);
//
//        return false;
//    }

//    @EventHandler
//    public void enterVehicle(VehicleEnterEvent e){
//        Entity entity = e.getEntered();
//        //只检测玩家上载具
//        if(!(entity instanceof Player player)) return;
//
//        //尝试注册hashmap
//        String name = player.getName();
//        if(!infos.containsKey(name)){
//            infos.put(name, new PlayerInfo());
//            System.out.println("玩家" + name + "成功注册");
//        }
//        //修改为在车上
//        infos.get(name).isOnVehicle = true;
//        System.out.println("玩家" + name + "上载具");
//    }
//
    @EventHandler
    public void leaveVehicle(VehicleExitEvent e){
        //只检测玩家下载具
        if(!(e.getVehicle() instanceof RideableMinecart minecart)) return;
        if(!(e.getExited() instanceof Player player)) return;
        if(minecart.hasGravity()) return;
        new BukkitRunnable(){
            @Override
            public void run(){
                Minecarts.stopFly(minecart);
            }
        }.runTaskLater(JavaPlugin.getPlugin(BetterMinecart.class), 10);
        player.addPotionEffect(new PotionEffect(
                PotionEffectType.SLOW_FALLING,
                200, 0, false
        ));
    }

    @EventHandler
    public void destroyVehicle(VehicleDestroyEvent e){
        Vehicle vehicle = e.getVehicle();
        if(!(vehicle instanceof RideableMinecart minecart)) return;
        Minecarts.soundCds.remove(minecart);
    }

    int count = 0;
    @EventHandler
    public void onVehicle_speed(VehicleMoveEvent e){
        //获取速度
        Vehicle vehicle = e.getVehicle();
        if(!(vehicle instanceof RideableMinecart minecart)) return;
        if(vehicle.isEmpty()) return;
        if(!(vehicle.getPassengers().get(0) instanceof Player)) return;

        if(count > 0){
            count--;
            return;
        }
        count = minecart.hasGravity() ? Minecarts.LISTEN_GAP : Minecarts.LISTEN_GAP / 2;

        double speed = Minecarts.getSpeed(e);
        double squaredSpeed = Minecarts.getSquaredSpeed(e);
        Vector velocity = Minecarts.getVelocity(e);
        if(speed > Minecarts.MAX * 2){
            Minecarts.minecartExplosion(minecart);
        }

        //获取乘客
        List<Entity> passengers = minecart.getPassengers();
        for(Entity passenger : passengers){
            if(!(passenger instanceof Player player)){
                continue;
            }
            player.sendActionBar(Component.text("当前速率/平面速率为 "
                    + String.format("%.2f", squaredSpeed) + '/'
                    + String.format("%.2f", speed) + '('
                    + String.format("%.2f", velocity.getX()) + ' '
                    + String.format("%.2f", velocity.getY()) + ' '
                    + String.format("%.2f", velocity.getZ()) + ')'
                    + " block/tick "
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
    public void onVehicle_fly(VehicleMoveEvent e) {
        Vehicle vehicle = e.getVehicle();
        if(vehicle.isEmpty()) return;
        if(!(vehicle instanceof RideableMinecart minecart)) return;
        if(!(minecart.getPassengers().get(0) instanceof Player)) return;

        if(!minecart.getDerailedVelocityMod().equals(Minecarts.derailedVelocityMod)){
            minecart.setDerailedVelocityMod(Minecarts.derailedVelocityMod);
        }

        double speed = Minecarts.getSpeed(e);

        if(minecart.hasGravity()){
            if(speed > Minecarts.TO_FLY) Minecarts.tryStartFly(minecart, speed);
        }
        else {
            minecart.getWorld().stopSound(SoundStop.named(Sound.ENTITY_MINECART_INSIDE));
            if(minecart.isInWater()){
                minecart.getWorld().playSound(
                        minecart.getLocation(),
                        Sound.ENTITY_AXOLOTL_SWIM,
                        (float) (speed/2), 1.0f
                );
            }
            else{
                minecart.getWorld().playSound(
                        minecart.getLocation(),
                        Sound.ENTITY_BREEZE_IDLE_GROUND,
                        (float) (speed/2), 0.8f
                );
            }
            if (speed < Minecarts.TO_FALL) {
                //加一点延迟 避免先于可能的碰撞触发导致碰撞不触发坠机
                new BukkitRunnable(){
                    @Override
                    public void run(){
                        Minecarts.stopFly(minecart);
                    }
                }.runTaskLater(JavaPlugin.getPlugin(BetterMinecart.class), 5);
            }

            double velocity_y = Minecarts.getVelocity(e).getY();

            if(!minecart.getFlyingVelocityMod().equals(Minecarts.flyingVelocityMod_land)){
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

