package com.duskrainfall.betterminecart.vehicle;

import com.duskrainfall.betterminecart.vehicle.boat.Boats;
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

        Entity vehicleEntity = player.getVehicle();
        if(vehicleEntity== null) return;

        var action = e.getAction();
        switch(vehicleEntity){
            case RideableMinecart minecart -> {
                synchronized (player){
                    if(cds.containsKey(player)){
                        int ticks = player.getTicksLived() - cds.get(player);
                        if(ticks < CD){
                            player.sendActionBar(Component.text("矿车控制还有 " + (CD - ticks) + " 刻冷却时间！", NamedTextColor.RED));
                            e.setCancelled(true);
                            return;
                        }
                    }
                    cds.put(player, player.getTicksLived());
                }
                //左击加速，右击空气减速
                switch(action){
                    case Action.LEFT_CLICK_AIR, Action.LEFT_CLICK_BLOCK -> {
                        Minecarts.maxSpeedUp(minecart, player);
                        e.setCancelled(true);
                    }
                    case Action.RIGHT_CLICK_AIR -> {
                        Minecarts.maxSpeedDn(minecart, player);
                        e.setCancelled(true);
                    }
                    default -> {
                        synchronized (player){
                            cds.put(player, player.getTicksLived() - CD);
                        }
                    }
                }
            }
            case Boat boat -> {
                synchronized (player){
                    if(cds.containsKey(player)){
                        int ticks = player.getTicksLived() - cds.get(player);
                        if(ticks < CD){
                            player.sendActionBar(Component.text("立体机动还有 " + (CD - ticks) + " 刻冷却时间！", NamedTextColor.RED));
                            e.setCancelled(true);
                            return;
                        }
                    }
                    cds.put(player, player.getTicksLived());
                }
                switch (action) {
                    case Action.LEFT_CLICK_AIR, Action.LEFT_CLICK_BLOCK -> {
                        e.setCancelled(true);
                    }
                    case Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK -> {
                        Boats.boatUp(boat, player);
                        e.setCancelled(true);
                    }
                    default -> {}
                }
            }
            default -> {}
        }
    }
}
