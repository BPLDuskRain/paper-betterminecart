package com.duskrainfall.betterminecart.vehicle;

import com.duskrainfall.betterminecart.vehicle.minecart.Minecarts;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.RideableMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class DriveListener implements Listener {
    public final static HashMap<Player, Integer> cds = new HashMap<>();
    private final static int CD = 10;

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
                    player.sendActionBar(Component.text("载具控制还有 " + (CD - ticks) + " 刻冷却时间！", NamedTextColor.RED));
                    e.setCancelled(true);
                    return;
                }
            }
            cds.put(player, player.getTicksLived());
        }

        Entity vehicleEntity = player.getVehicle();
        if(vehicleEntity instanceof RideableMinecart minecart){
            //左击加速，右击空气减速
            switch(e.getAction()){
                case Action.LEFT_CLICK_AIR:
                case Action.LEFT_CLICK_BLOCK:
                    Minecarts.maxSpeedUp(minecart, player);
                    e.setCancelled(true);
                    break;
                case Action.RIGHT_CLICK_AIR:
                    Minecarts.maxSpeedDn(minecart, player);
                    e.setCancelled(true);
                    break;
                default:
                    synchronized (player){
                        cds.put(player, player.getTicksLived() - CD);
                    }
            }
        }
    }
}
