package com.duskrainfall.betterminecart.spring;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;

public class DropItemListener implements Listener {
    private final Plugin plugin;
    private final HashMap<Player, Long> cds = new HashMap<>();
    private final static long CD = 2000L;

    private final static String CREATE = "spring.create";
    private final static String REMOVE = "spring.remove";

    private final SpringEditor springEditor;
    public DropItemListener(Plugin plugin){
        this.plugin = plugin;
        springEditor = new SpringEditor(plugin);
    }

    public SpringEditor getSpringEditer(){
        return springEditor;
    }

    @EventHandler
    public void whenDropping(PlayerDropItemEvent e) {
        Player player =  e.getPlayer();
        if(!player.isInWater()){
            return;
        }
        Item item =  e.getItemDrop();
        switch (item.getItemStack().getType()){
            case Material.FIRE_CHARGE:
                if(!player.hasPermission(CREATE)){
                    player.sendActionBar(Component.text("你没有权限创建温泉！", NamedTextColor.DARK_RED));
                    return;
                }
                if(cds.containsKey(player)){
                    if(System.currentTimeMillis() - cds.get(player) < CD){
                        player.sendActionBar(Component.text("创建温泉有 " + CD/1000 + " 秒冷却时间！", NamedTextColor.DARK_RED));
                        return;
                    }
                }
                cds.put(player, System.currentTimeMillis());
                player.sendActionBar(Component.text("尝试创建温泉", NamedTextColor.GOLD));

                springEditor.createSpring(item, player);

                break;
            case Material.SNOWBALL:
                if(!player.hasPermission(REMOVE)){
                    player.sendActionBar(Component.text("你没有权限移除温泉！", NamedTextColor.DARK_RED));
                    return;
                }

                if(cds.containsKey(player)){
                    if(System.currentTimeMillis() - cds.get(player) < CD){
                        player.sendActionBar(Component.text("移除温泉有 " + CD/1000 + " 秒冷却时间！", NamedTextColor.DARK_RED));
                        return;
                    }
                }
                cds.put(player, System.currentTimeMillis());
                player.sendActionBar(Component.text("尝试移除温泉", NamedTextColor.GOLD));

                springEditor.removeSpring(item, player);

                break;
        }

    }
}
