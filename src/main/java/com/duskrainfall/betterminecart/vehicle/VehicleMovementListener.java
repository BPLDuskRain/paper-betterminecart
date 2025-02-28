package com.duskrainfall.betterminecart.vehicle;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleMoveEvent;

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
//    @EventHandler
//    public void leaveVehicle(VehicleExitEvent e){
//        Entity entity = e.getExited();
//        //只检测玩家下载具
//        if(!(entity instanceof Player player)) return;
//
//        //尝试注册hashmap
//        String name = player.getName();
//        if(!infos.containsKey(name)){
//            infos.put(name, new PlayerInfo());
//            System.out.println("玩家" + name + "成功注册");
//        }
//        //修改为不在车上
//        infos.get(name).isOnVehicle = false;
//        System.out.println("玩家" + name + "下载具");
//    }

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

        double speed = Minecarts.getSpeed(e);
        double velocity = Minecarts.getVelocity(e);

        //获取乘客
        List<Entity> passengers = vehicle.getPassengers();
        for(Entity passenger : passengers){
            if(!(passenger instanceof Player player)){
                continue;
            }
            player.sendActionBar(Component.text("当前速度为 "
                    + String.format("%.2f", velocity)
                    + '(' + String.format("%.2f", speed) + ')'
                    + " block/tick ", NamedTextColor.GREEN));
        }
    }

    @EventHandler
    public void onVehicle_fly(VehicleMoveEvent e) {
        Vehicle vehicle = e.getVehicle();
        if(vehicle.isEmpty()) return;
        if(!(vehicle instanceof Minecart minecart)) return;
        if(!(vehicle.getPassengers().get(0) instanceof Player)) return;

        double speed = Minecarts.getSpeed(e);

        if(minecart.hasGravity()){
            if(speed > 0.5d) Minecarts.tryStartFly(minecart, speed);
        }
        else{
            if(speed < 0.5d) Minecarts.stopFly(minecart);
            Minecarts.tryStopFly(minecart);
        }
    }
}

