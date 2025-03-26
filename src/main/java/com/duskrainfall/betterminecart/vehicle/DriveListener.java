package com.duskrainfall.betterminecart.vehicle;

import com.duskrainfall.betterminecart.vehicle.minecart.Minecarts;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.RideableMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class DriveListener implements Listener {
    private final static HashMap<Player, Integer> cds = new HashMap<>();
    private final static int CD = 10;

    @EventHandler
    public void onDeath(PlayerDeathEvent e){
        cds.remove(e.getPlayer());
    }

    @EventHandler
    public void onClick(PlayerInteractEvent e){
        Player player = e.getPlayer();
        ItemStack item_main = player.getInventory().getItemInMainHand();

        if(item_main.getType() != Minecarts.CONTROL_ITEM) return;
        if(!player.isInsideVehicle()) return;

        synchronized (player){
            if(cds.containsKey(player)){
                int ticks = player.getTicksLived() - cds.get(player);
                if(ticks < CD){
                    player.sendActionBar(Component.text("极速调整还有 " + (CD - ticks) + " 刻冷却时间！", NamedTextColor.RED));
                    e.setCancelled(true);
                    return;
                }
            }
            cds.put(player, player.getTicksLived());
        }
        //左击加速，右击空气减速
        Entity vehicleEntity = player.getVehicle();
        if(vehicleEntity instanceof RideableMinecart minecart){
            switch(e.getAction()){
                case Action.LEFT_CLICK_AIR:
                case Action.LEFT_CLICK_BLOCK:
                    Minecarts.maxSpeedUpEvent(minecart, player);
                    e.setCancelled(true);
                    break;
                case Action.RIGHT_CLICK_AIR:
                    Minecarts.maxSpeedDnEvent(minecart, player);
                    e.setCancelled(true);
                    break;
                default:
                    synchronized (player){
                        cds.put(player, player.getTicksLived() - CD);
                    }
            }
        }
        else if(vehicleEntity instanceof Boat boat){

        }
    }

//    @EventHandler
//    public void onLeftClick(PlayerAnimationEvent event) {
//        Player player = event.getPlayer();
//        ItemStack item = player.getInventory().getItemInMainHand();
//
//        if (player.isInsideVehicle() && item.getType() == Material.COMPASS) {
//            if(player.getVehicle() instanceof Minecart minecart){
//                minecart.setMaxSpeed(minecart.getMaxSpeed()*2);
//                if(minecart.getMaxSpeed() > MAX) minecart.setMaxSpeed(MAX);
//                player.sendMessage("载具最大速度已经增加到" + minecart.getMaxSpeed() + "block/tick");
//            }
//        }
//    }
}
