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
import java.util.List;

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
                        player.sendActionBar(Component.text("编组失败：不能和自己编组", NamedTextColor.RED));
                        return;
                    }

                    if(Minecarts.hookedMap.containsKey(entity)){
                        player.sendActionBar(Component.text("编组失败：已经编组到列车", NamedTextColor.RED));
                        return;
                    }

                    if(!Minecarts.cars.containsKey(minecart)){
                        Minecarts.cars.put(minecart, new ArrayList<>());
                    }

                    List<Entity> list = Minecarts.cars.get(minecart);
                    if(list.size() < Minecarts.MAX_CAR_NUM){
                        list.add(entity);
                    }
                    else{
                        player.sendActionBar(Component.text("编组失败：单机编组数量过大", NamedTextColor.RED));
                        return;
                    }
                    Minecarts.hookedMap.put(entity, minecart);
                }
                player.sendActionBar(Component.text("编组成功", NamedTextColor.GREEN));
            }
            default -> {}
        }
    }
}
