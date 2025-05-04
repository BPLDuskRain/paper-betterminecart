package com.duskrainfall.betterminecart.vehicle.minecart;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.RideableMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;

import java.util.ArrayList;

public class MinecartConnectListener implements Listener {
    @EventHandler
    public void OnFishing(PlayerFishEvent e){
        switch (e.getState()){
            case CAUGHT_ENTITY -> {
                if(!(e.getCaught() instanceof RideableMinecart minecart)) return;

                e.setCancelled(true);

                Player player = e.getPlayer();
                if(!player.isInsideVehicle()) return;

                Entity entity = player.getVehicle();
                if(entity != null){
                    if(entity.equals(minecart)) {
                        player.sendActionBar(Component.text("编组失败", NamedTextColor.RED));
                        return;
                    }
                    Minecarts.hookedMap.put(entity, minecart);
                    if(!Minecarts.cars.containsKey(minecart)){
                        Minecarts.cars.put(minecart, new ArrayList<>());
                    }
                    Minecarts.cars.get(minecart).add(entity);
                }
                player.sendActionBar(Component.text("编组成功", NamedTextColor.GREEN));
            }
            default -> {}
        }
    }
}
