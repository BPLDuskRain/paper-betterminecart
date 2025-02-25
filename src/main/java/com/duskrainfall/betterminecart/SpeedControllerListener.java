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
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class SpeedControllerListener implements Listener {
    final public double MAX = 1.6d;
    final public double MIN = 0.05d;
    final public Material CONTROL_ITEM = Material.RECOVERY_COMPASS;

    @EventHandler
    public void onClick(PlayerInteractEvent e){
        Player player = e.getPlayer();
        ItemStack item_main = player.getInventory().getItemInMainHand();

        if(item_main.getType() != CONTROL_ITEM) return; //必须手持对应物品
        if(!player.isInsideVehicle()) return; //必须在载具内
        if(!(player.getVehicle() instanceof Minecart minecart)) return; //载具必须是矿车
        //左击空气加速，右击减速
        switch(e.getAction()){
            case Action.LEFT_CLICK_AIR :
                if(minecart.getMaxSpeed() >= MAX) {
                    minecart.setMaxSpeed(MAX);
                    player.sendActionBar(Component.text("太快了，不能再加了！", NamedTextColor.RED));
                }
                else{
                    minecart.setMaxSpeed(minecart.getMaxSpeed()*2);
                    player.sendActionBar(Component.text("载具最大速度已经增加到 " +
                            Math.min(minecart.getMaxSpeed(), 1.5d) + " block/tick", NamedTextColor.AQUA));

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
                break;
            case Action.RIGHT_CLICK_AIR:
                if(minecart.getMaxSpeed() <= MIN) {
                    minecart.setMaxSpeed(MIN);
                    player.sendActionBar(Component.text("太慢了，不能再减了！", NamedTextColor.RED));
                }
                else{
                    minecart.setMaxSpeed(minecart.getMaxSpeed()/2);
                    player.sendActionBar(Component.text("载具最大速度已经限制到 " +
                            minecart.getMaxSpeed() + " block/tick", NamedTextColor.AQUA));

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
