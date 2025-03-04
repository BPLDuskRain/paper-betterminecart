package com.duskrainfall.betterminecart.spring;

import com.duskrainfall.betterminecart.BetterMinecart;
import com.duskrainfall.betterminecart.mapper.SpringBlocksMapper;
import com.duskrainfall.betterminecart.records.SpringBlock;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Random;

public class Springs {
    private final static HashSet<Location> los = new HashSet<>();
    private final static int CREATE_RADIUS = 5;
    private final static int REMOVE_RADIUS = 10;
    //    private final static long CONTINUE_TIME = 60000L;
    private final static Plugin plugin = JavaPlugin.getPlugin(BetterMinecart.class);

    private static void createSpringAnimation(Entity entity){
        Location item_location = entity.getLocation();
        entity.getWorld().spawnParticle(Particle.FLAME, item_location,
                50, 1, 1, 1
        );
        entity.getWorld().playSound(
                item_location,
                Sound.BLOCK_LAVA_EXTINGUISH,
                1.0f, 1.0f
        );
    }
    private static void addSpringBlocks(Block centreBlock){
        changed = true;
        for(int x = -CREATE_RADIUS; x <= CREATE_RADIUS; ++x){
            for(int y = 0; y <= CREATE_RADIUS; ++y){
                for(int z = -CREATE_RADIUS; z <= CREATE_RADIUS; ++z){
                    Location location = centreBlock.getLocation().clone().add(x, y, z);
                    if(location.getBlock().getType() == Material.WATER){
                        los.add(location);
                    }
                }
            }
        }
    }

    private static void removeSpringAnimation(Entity entity){
        Location item_location = entity.getLocation();
        entity.getWorld().spawnParticle(Particle.SNOWFLAKE, item_location,
                50, 1, 1, 1
        );
        entity.getWorld().playSound(
                item_location,
                Sound.BLOCK_SNOW_STEP,
                1.0f, 1.0f
        );
    }
    private static void delSpringBlocks(Block centreBlock){
        changed = true;
        for(int x = -REMOVE_RADIUS; x <= REMOVE_RADIUS; ++x){
            for(int y = -REMOVE_RADIUS; y <= REMOVE_RADIUS; ++y){
                for(int z = -REMOVE_RADIUS; z <= REMOVE_RADIUS; ++z){
                    Location location = centreBlock.getLocation().clone().add(x, y, z);
                    if(location.getBlock().getType() == Material.WATER){
                        los.remove(location);
                    }
                }
            }
        }
    }

    public final static ArrayList<BukkitRunnable> tasks = new ArrayList<>();

    // 创建区域
    public static void createSpring(Entity entity, long duration){
        createSpringAnimation(entity);
        Block centreBlock = entity.getLocation().getBlock();
        addSpringBlocks(centreBlock);
        var task = new BukkitRunnable(){
            @Override
            public void run(){
                delSpringBlocks(centreBlock);
                tasks.remove(this);
            }
        };
        tasks.add(task);
        task.runTaskLater(JavaPlugin.getPlugin(BetterMinecart.class), duration);
    }
    public static void createSpring(Entity entity, Player player) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!entity.isInWater()) {
                    player.sendActionBar(Component.text("创建温泉失败", NamedTextColor.DARK_RED));
                    return;
                }

                createSpringAnimation(entity);

                Block centreBlock = entity.getLocation().getBlock();
                if (centreBlock.getType() != Material.WATER) {
                    player.sendActionBar(Component.text("创建温泉失败", NamedTextColor.DARK_RED));
                    return;
                }

                addSpringBlocks(centreBlock);

                entity.remove();
                player.sendActionBar(Component.text("成功创建温泉", NamedTextColor.DARK_GREEN));
            }
        }.runTaskLater(plugin, 20);
    }

    // 移除区域
    public static void removeSpring(Entity entity, Player player){
        new BukkitRunnable(){
            @Override
            public void run(){
                if(!entity.isInWater()){
                    player.sendActionBar(Component.text("移除温泉失败", NamedTextColor.DARK_RED));
                    return;
                }

                removeSpringAnimation(entity);

                Block centreBlock = entity.getLocation().getBlock();
                if(centreBlock.getType() != Material.WATER) {
                    player.sendActionBar(Component.text("移除温泉失败", NamedTextColor.DARK_RED));
                    return;
                }

                delSpringBlocks(centreBlock);

                entity.remove();
                player.sendActionBar(Component.text("成功移除温泉", NamedTextColor.DARK_GREEN));

            }
        }.runTaskLater(plugin, 20);
    }

    // 温泉效果
    public static void springEffect(){
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
                                        200, 0, true
                                ));
                                livingEntity.addPotionEffect(new PotionEffect(
                                        PotionEffectType.NAUSEA,
                                        40, 0, true, false, false
                                ));
                            }
                            livingEntity.addPotionEffect(new PotionEffect(
                                    PotionEffectType.REGENERATION,
                                    200, 1, true
                            ));
                            livingEntity.addPotionEffect(new PotionEffect(
                                    PotionEffectType.RESISTANCE,
                                    200, 1, true
                            ));
                        }
                    }
                }
            }
        }.runTaskTimer(plugin, 0, 20 + random.nextInt(60));
    }

    //温泉持久化
    public static boolean canSaved = true;
    public static SqlSessionFactory sqlSessionFactory;
    static {
        try {
            sqlSessionFactory = new SqlSessionFactoryBuilder()
                    .build(new FileInputStream("plugins/betterminecart-mybatis-config.xml"));
        } catch (IOException e) {
            canSaved = false;
        }
    }

    public static Location toLocation(SpringBlock springBlock){
        return new Location(
                Bukkit.getWorld(springBlock.world()),
                springBlock.x(),
                springBlock.y(),
                springBlock.z()
                );
    }
    public static void readBlocks(){
        if(!canSaved) return;
        try(SqlSession session = sqlSessionFactory.openSession(true)){
            SpringBlocksMapper springBlocksMapper = session.getMapper(SpringBlocksMapper.class);
            for(var block : springBlocksMapper.getBlocks()){
                los.add(toLocation(block));
            }
        }
    }

    private static long count= 1;
    public static SpringBlock toSpringBlock(Location location){
        return new SpringBlock(
                count++,
                location.getWorld().getName(),
                location.getX(),
                location.getY(),
                location.getZ()
        );
    }
    public static boolean changed = false;
    public static void writeBlocks(){
        if(!canSaved) return;
        if(!changed) return;
        try(SqlSession session = sqlSessionFactory.openSession(true)){
            SpringBlocksMapper springBlocksMapper = session.getMapper(SpringBlocksMapper.class);
            springBlocksMapper.clear();
            for(var lo : los){
                springBlocksMapper.insertBlock(toSpringBlock(lo));
            }
        }
    }
}
