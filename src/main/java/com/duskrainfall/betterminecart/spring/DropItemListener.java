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

import java.util.HashMap;

public class DropItemListener implements Listener {
    private final HashMap<Player, Integer> cds = new HashMap<>();
    private final static int CD = 40;

    private final static String CREATE = "spring.create";
    private final static String REMOVE = "spring.remove";

    @EventHandler
    public void onDeath(PlayerDeathEvent e){
        cds.remove(e.getPlayer());
    }

    @EventHandler
    public void whenDropping(PlayerDropItemEvent e) {
        Player player =  e.getPlayer();
        if(player.getWorld() == Bukkit.getWorld("world_nether")) return;
        if(!player.isInWater()) return;

        Item item =  e.getItemDrop();
        switch (item.getItemStack().getType()){
            case Material.FIRE_CHARGE:
                if(!player.hasPermission(CREATE)){
                    player.sendActionBar(Component.text("你没有权限创建温泉！", NamedTextColor.DARK_RED));
                    return;
                }

                synchronized (player){
                    if(cds.containsKey(player)){
                        int ticks = player.getTicksLived() - cds.get(player);
                        if(ticks < CD){
                            player.sendActionBar(Component.text("创建温泉还有 " + (CD - ticks) + " 刻冷却时间！", NamedTextColor.DARK_RED));
                            e.setCancelled(true);
                            return;
                        }
                    }
                    cds.put(player, player.getTicksLived());
                }

                player.sendActionBar(Component.text("尝试创建温泉", NamedTextColor.GOLD));

                Springs.createSpring(item, player);

                break;
            case Material.SNOWBALL:
                if(!player.hasPermission(REMOVE)){
                    player.sendActionBar(Component.text("你没有权限移除温泉！", NamedTextColor.DARK_RED));
                    return;
                }

                synchronized (player){
                    if(cds.containsKey(player)){
                        int ticks = player.getTicksLived() - cds.get(player);
                        if(ticks < CD){
                            player.sendActionBar(Component.text("移除温泉还有 " + (CD - ticks) + " 刻冷却时间！", NamedTextColor.DARK_RED));
                            e.setCancelled(true);
                            return;
                        }
                    }
                    cds.put(player, player.getTicksLived());
                }

                player.sendActionBar(Component.text("尝试移除温泉", NamedTextColor.GOLD));

                Springs.removeSpring(item, player);

                break;
        }

    }
}
