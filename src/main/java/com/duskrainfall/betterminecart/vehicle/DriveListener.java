package com.duskrainfall.betterminecart.vehicle;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.RideableMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class DriveListener implements Listener {
    private final static HashMap<Player, Long> cds = new HashMap<>();
    private final static long CD = 500L;

    @EventHandler
    public void onClick(PlayerInteractEvent e){
        Player player = e.getPlayer();
        ItemStack item_main = player.getInventory().getItemInMainHand();

        if(item_main.getType() != Minecarts.CONTROL_ITEM) return; //必须手持对应物品
        if(!player.isInsideVehicle()) return; //必须在载具内
        if(!(player.getVehicle() instanceof RideableMinecart minecart)) return; //载具必须是矿车
        //左击加速，右击空气减速
        if(cds.containsKey(player)){
            if(System.currentTimeMillis() - cds.get(player) < CD){
                player.sendActionBar(Component.text("极速调整有 " + CD + " 毫秒冷却时间！", NamedTextColor.RED));
                e.setCancelled(true);
                return;
            }
        }
        cds.put(player, System.currentTimeMillis());
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
