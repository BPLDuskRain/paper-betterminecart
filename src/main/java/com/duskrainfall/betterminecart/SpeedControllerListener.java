package com.duskrainfall.betterminecart;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class SpeedControllerListener implements Listener {
    final public double MAX = 1.6d;
    final public double MIN = 0.1d;

    @EventHandler
    public void onClick(PlayerInteractEvent e){
        Player player = e.getPlayer();
        ItemStack item_main = player.getInventory().getItemInMainHand();
        ItemStack item_off = player.getInventory().getItemInOffHand();

        if(!player.isInsideVehicle()) return;
        //副手加速，主手减速
        if (item_off.getType() == Material.COMPASS) {
            if(player.getVehicle() instanceof Minecart minecart){
                minecart.setMaxSpeed(minecart.getMaxSpeed()*2);
                if(minecart.getMaxSpeed() > MAX) minecart.setMaxSpeed(MAX);

                player.sendActionBar(Component.text("载具最大速度已经增加到 " + Math.min(minecart.getMaxSpeed(), 1.5d) + " block/tick", NamedTextColor.RED));
//                player.sendMessage("载具最大速度已经增加到 " + minecart.getMaxSpeed() + " block/tick");

                minecart.getWorld().spawnParticle(
                        Particle.FIREWORK, // 粒子类型
                        minecart.getLocation().add(0, 1, 0), // 位置
                        20, // 数量
                        0.5, 0.5, 0.5, // 偏移量
                        0.1 // 速度
                );

                player.playSound(
                        player.getLocation(),
                        Sound.ENTITY_FIREWORK_ROCKET_LAUNCH,
                        8.0f,
                        0.2f
                );
            }
        }
        else if (item_main.getType() == Material.COMPASS) {
            if(player.getVehicle() instanceof Minecart minecart){
                minecart.setMaxSpeed(minecart.getMaxSpeed()/2);
                if(minecart.getMaxSpeed() < MIN) minecart.setMaxSpeed(MIN);
                player.sendActionBar(Component.text("载具最大速度已经限制到 " + minecart.getMaxSpeed() + " block/tick", NamedTextColor.RED));
//                player.sendMessage("载具最大速度已经限制到 " + minecart.getMaxSpeed() + " block/tick");

                minecart.getWorld().spawnParticle(
                        Particle.LAVA, // 粒子类型
                        minecart.getLocation().add(0, 1, 0), // 位置
                        20, // 数量
                        0.5, 0.5, 0.5, // 偏移量
                        0.1 // 速度
                );

                player.playSound(
                        player.getLocation(),
                        Sound.BLOCK_ANVIL_HIT,
                        8.0f,
                        0.2f
                );
            }
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
