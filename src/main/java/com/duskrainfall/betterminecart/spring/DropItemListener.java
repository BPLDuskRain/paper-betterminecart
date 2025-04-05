package com.duskrainfall.betterminecart.spring;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;

public class DropItemListener implements Listener {
    @EventHandler
    public void onDeath(PlayerDeathEvent e){
        Springs.cds.remove(e.getPlayer());
    }

    @EventHandler
    public void whenDropping(PlayerDropItemEvent e) {
        Player player =  e.getPlayer();
        if(player.getWorld() == Bukkit.getWorld("world_nether")) return;
        if(!player.isInWater()) return;

        Item item =  e.getItemDrop();
        switch (item.getItemStack().getType()){
            case Material.FIRE_CHARGE:
                if(!player.hasPermission(Springs.CREATE)){
                    player.sendActionBar(Component.text("你没有权限创建温泉！", NamedTextColor.DARK_RED));
                    return;
                }

                if(Springs.isCooling(player, "温泉创建")) {
                    e.setCancelled(true);
                    return;
                }

                player.sendActionBar(Component.text("尝试创建温泉", NamedTextColor.GOLD));

                Springs.createSpring(item, player);

                break;
            case Material.SNOWBALL:
                if(!player.hasPermission(Springs.REMOVE)){
                    player.sendActionBar(Component.text("你没有权限移除温泉！", NamedTextColor.DARK_RED));
                    return;
                }

                if(Springs.isCooling(player, "温泉移除")) {
                    e.setCancelled(true);
                    return;
                }

                player.sendActionBar(Component.text("尝试移除温泉", NamedTextColor.GOLD));

                Springs.removeSpring(item, player);

                break;
        }

    }
}
