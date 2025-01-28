package com.duskrainfall.betterminecart;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
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

public class VehicleSpeed implements Listener {

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
    public void onVehicle(VehicleMoveEvent e){
        if(count > 0){
            count--;
            return;
        }
        count = 40;
        //获取速度
        Vehicle vehicle = e.getVehicle();
        if(vehicle.isEmpty()) return;
//        Vector vector = vehicle.getVelocity();
//        double x = vector.getX();
//        double y = vector.getY();
//        double z = vector.getZ();
//        double speed = Math.sqrt(x*x+y*y+z*z);

        Location from = e.getFrom();
        Location to = e.getTo();
        double x = Math.abs(from.getX() - to.getX());
        double y = Math.abs(from.getY() - to.getY());
        double z = Math.abs(from.getZ() - to.getZ());
        double speed = Math.sqrt(x*x+y*y+z*z);

        //获取乘客
        List<Entity> passengers = vehicle.getPassengers();
        for(Entity passenger : passengers){
            if(!(passenger instanceof Player player)){
                continue;
            }
            String name = player.getName();
            if(vehicle instanceof Minecart minecart){
                player.sendActionBar(Component.text("当前速度为 " + String.format("%.2f", speed) + " block/tick " +
                        "单向限速为 " + String.format("%.2f", Math.min(minecart.getMaxSpeed(), 1.5d)) + " block/tick", NamedTextColor.GREEN));
//                player.sendMessage("当前速度为 " + String.format("%.2f", speed) + " block/tick " +
//                        "最大速度为 " + String.format("%.2f", Math.min(minecart.getMaxSpeed(), 1.5d)) + " block/tick");
            }
            else{
                player.sendActionBar(Component.text("当前速度为 " + String.format("%.2f", speed) + " block/tick ", NamedTextColor.GREEN));
//                player.sendMessage("当前速度为 " + String.format("%.2f", speed) + " block/tick ");
            }
        }
    }
}

