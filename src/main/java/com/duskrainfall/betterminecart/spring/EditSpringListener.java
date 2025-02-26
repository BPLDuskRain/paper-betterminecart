package com.duskrainfall.betterminecart.spring;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

public class EditSpringListener implements Listener {
    private final Plugin plugin;
    private final HashMap<Player, Long> cds = new HashMap<>();
    private final static long CD = 2000L;
    private final HashSet<Location> los = new HashSet<>();
    private final static int RADIUS = 5;
//    private final static long CONTINUE_TIME = 60000L;
    private final static String CREATE = "spring.create";
    private final static String REMOVE = "spring.remove";

    public EditSpringListener(Plugin plugin){
        this.plugin = plugin;
    }

    // 创建区域
    private void createSpring(Item item, Player player){
        new BukkitRunnable(){
            @Override
            public void run(){
                if(!item.isInWater()){
                    player.sendActionBar(Component.text("创建温泉失败", NamedTextColor.DARK_RED));
                    return;
                }
                Location item_location = item.getLocation();
                player.getWorld().spawnParticle(Particle.FLAME, item_location,
                        50, 1, 1, 1
                );
                player.getWorld().playSound(
                        item_location,
                        Sound.BLOCK_LAVA_EXTINGUISH,
                        1.0f, 1.0f
                );

                Block centreBlock = item_location.getBlock();
                if(centreBlock.getType() != Material.WATER) {
                    player.sendActionBar(Component.text("创建温泉失败", NamedTextColor.DARK_RED));
                    return;
                }

                for(int x = -RADIUS; x <= RADIUS; ++x){
                    for(int y = 0; y <= RADIUS; ++y){
                        for(int z = -RADIUS; z <= RADIUS; ++z){
                            Location location = centreBlock.getLocation().clone().add(x, y, z);
                            if(location.getBlock().getType() == Material.WATER){
                                los.add(location);
                            }
                        }
                    }
                }

                item.remove();
                player.sendActionBar(Component.text("成功创建温泉", NamedTextColor.DARK_GREEN));

            }
        }.runTaskLater(plugin, 20);
    }

    // 温泉效果
    public void springEffect(){
        Random random = new Random(System.currentTimeMillis());
        new BukkitRunnable(){
            @Override
            public void run(){
                for (Location lo : los) {
                    Location loTmp = lo.clone().add(0, 1, 0);
                    if(loTmp.getBlock().getType() == Material.WATER) continue;
                    float pos = random.nextFloat();
                    lo.getWorld().spawnParticle(Particle.CLOUD, lo,
                            1, pos, pos, pos, 0.02
                    );
                }
            }
        }.runTaskTimer(plugin, 0, 40 + random.nextInt(40));
        new BukkitRunnable(){
            @Override
            public void run(){
                for (Location lo : los) {
                    Location loTmp = lo.clone().add(0, 1, 0);
                    if(loTmp.getBlock().getType() == Material.WATER) continue;
                    float pos = random.nextFloat();
                    lo.getWorld().spawnParticle(Particle.WHITE_SMOKE, lo,
                            2, pos, pos, pos, 0.05
                    );
                }
            }
        }.runTaskTimer(plugin, 0, 20 + random.nextInt(30));
        new BukkitRunnable(){
            @Override
            public void run(){
                for (Location lo : los) {
                    float pos = random.nextFloat();
                    lo.getWorld().spawnParticle(Particle.BUBBLE, lo,
                            3, pos, pos, pos, 0.02
                    );
                }
            }
        }.runTaskTimer(plugin, 0, 20 + random.nextInt(40));
        new BukkitRunnable(){
            @Override
            public void run(){
                for (Location lo : los) {
                    Collection<Entity> entities = lo.getNearbyEntities(2, 1, 2);
                    if(!entities.isEmpty()){
                        for (Entity entity : entities) {
                            if(!(entity instanceof LivingEntity livingEntity)) continue;
                            if(livingEntity.isInWater()){
                                livingEntity.addPotionEffect(new PotionEffect(
                                        PotionEffectType.CONDUIT_POWER,
                                        200, 1, true
                                ));
                                livingEntity.addPotionEffect(new PotionEffect(
                                        PotionEffectType.NAUSEA,
                                        40, 1, true, false, false
                                ));
                            }
                            livingEntity.addPotionEffect(new PotionEffect(
                                    PotionEffectType.REGENERATION,
                                    200, 2, true
                            ));
                            livingEntity.addPotionEffect(new PotionEffect(
                                    PotionEffectType.RESISTANCE,
                                    200, 2, true
                            ));
                        }
                    }
                }
            }
        }.runTaskTimer(plugin, 0, 20 + random.nextInt(60));
    }

    // 移除效果
    private void removeSpring(Item item, Player player){
        new BukkitRunnable(){
            @Override
            public void run(){
                if(!item.isInWater()){
                    player.sendActionBar(Component.text("移除温泉失败", NamedTextColor.DARK_RED));
                    return;
                }
                Location item_location = item.getLocation();
                player.getWorld().spawnParticle(Particle.SNOWFLAKE, item_location,
                        50, 1, 1, 1
                );
                player.getWorld().playSound(
                        item_location,
                        Sound.BLOCK_SNOW_STEP,
                        1.0f, 1.0f
                );

                Block centreBlock = item_location.getBlock();
                if(centreBlock.getType() != Material.WATER) {
                    player.sendActionBar(Component.text("移除温泉失败", NamedTextColor.DARK_RED));
                    return;
                }

                for(int x = -RADIUS; x <= RADIUS; ++x){
                    for(int y = 0; y <= RADIUS; ++y){
                        for(int z = -RADIUS; z <= RADIUS; ++z){
                            Location location = centreBlock.getLocation().clone().add(x, y, z);
                            if(location.getBlock().getType() == Material.WATER){
                                los.remove(location);
                            }
                        }
                    }
                }

                item.remove();
                player.sendActionBar(Component.text("成功移除温泉", NamedTextColor.DARK_GREEN));

            }
        }.runTaskLater(plugin, 20);
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

                createSpring(item, player);

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

                removeSpring(item, player);

                break;
        }

    }
}
