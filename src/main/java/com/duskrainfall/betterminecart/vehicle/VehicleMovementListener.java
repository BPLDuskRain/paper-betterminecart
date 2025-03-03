package com.duskrainfall.betterminecart.vehicle;

import com.duskrainfall.betterminecart.BetterMinecart;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
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
        if(!(e.getVehicle() instanceof Minecart minecart)) return;
        if(!(e.getExited() instanceof Player player)) return;
        if(minecart.hasGravity()) return;
        Minecarts.stopFly(minecart);
        player.addPotionEffect(new PotionEffect(
                PotionEffectType.SLOW_FALLING,
                200, 0, false
        ));
    }

    int count = 0;
    @EventHandler
    public void onVehicle_speed(VehicleMoveEvent e){
        if(count > 0){
            count--;
            return;
        }
        count = 20;
        //获取速度
        Vehicle vehicle = e.getVehicle();
        if(vehicle.isEmpty()) return;
        if(!(vehicle.getPassengers().get(0) instanceof Player)) return;

        double speed = Minecarts.getSpeed(e);
        Vector velocity = Minecarts.getVelocity(e);

        //获取乘客
        List<Entity> passengers = vehicle.getPassengers();
        for(Entity passenger : passengers){
            if(!(passenger instanceof Player player)){
                continue;
            }
            player.sendActionBar(Component.text("当前速率/各向速率为 "
                    + String.format("%.2f", speed) + '('
                    + String.format("%.2f", velocity.getX()) + ' '
                    + String.format("%.2f", velocity.getY()) + ' '
                    + String.format("%.2f", velocity.getZ()) + ')'
                    + " block/tick "
                    , NamedTextColor.GREEN));
            if(!vehicle.hasGravity() && speed < Minecarts.TO_FALL + 0.2d){
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
                }.runTaskLater(JavaPlugin.getPlugin(BetterMinecart.class), 5);
            }
        }
    }

    @EventHandler
    public void onVehicle_fly(VehicleMoveEvent e) {
        Vehicle vehicle = e.getVehicle();
        if(vehicle.isEmpty()) return;
        if(!(vehicle instanceof Minecart minecart)) return;
        if(!(vehicle.getPassengers().get(0) instanceof Player)) return;

        if(!minecart.getDerailedVelocityMod().equals(Minecarts.derailedVelocityMod)){
            minecart.setDerailedVelocityMod(Minecarts.derailedVelocityMod);
        }

        double speed = Minecarts.getSpeed(e);

        if(minecart.hasGravity()){
            if(speed > Minecarts.TO_FLY) Minecarts.tryStartFly(minecart, speed);
        }
        else {
            if (speed < Minecarts.TO_FALL) {
                //加一点延迟 避免先于可能的碰撞触发导致碰撞不触发坠机
                new BukkitRunnable(){
                    @Override
                    public void run(){
                        Minecarts.stopFly(minecart);
                    }
                }.runTaskLater(JavaPlugin.getPlugin(BetterMinecart.class), 5);
            }
            Minecarts.flyControl(minecart);
            if (!Minecarts.tryLanding(minecart, speed, Minecarts.getVelocity(e).getY())) {
                Minecarts.tryStopFly(minecart);
            }
        }
    }
}

