package com.duskrainfall.betterminecart.vehicle;

import com.duskrainfall.betterminecart.vehicle.boat.Boats;
import com.duskrainfall.betterminecart.vehicle.minecart.Minecarts;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.RideableMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class DriveListener implements Listener {
    @EventHandler
    public void onClick(PlayerInteractEvent e){
        Player player = e.getPlayer();
        ItemStack item_main = player.getInventory().getItemInMainHand();

        if(item_main.getType() != Minecarts.CONTROL_ITEM) return;
        if(!player.isInsideVehicle()) return;

        Entity vehicleEntity = player.getVehicle();
        if(vehicleEntity == null) return;

        var action = e.getAction();
        switch(vehicleEntity){
            case RideableMinecart minecart -> {
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
                    default -> {}
                }
            }
            case Boat boat -> {
                switch (action) {
                    case Action.LEFT_CLICK_AIR, Action.LEFT_CLICK_BLOCK -> {
                        Boats.magnet(boat, player);
                        e.setCancelled(true);
                    }
                    case Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK -> {
                        Boats.jump(boat, player);
                        e.setCancelled(true);
                    }
                    default -> {}
                }
            }
            default -> {}
        }
    }
}
