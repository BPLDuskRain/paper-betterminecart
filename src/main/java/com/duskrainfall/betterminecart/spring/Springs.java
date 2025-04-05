package com.duskrainfall.betterminecart.spring;

import com.duskrainfall.betterminecart.BetterMinecart;
import com.duskrainfall.betterminecart.bean.SpringBlock_Data;
import com.duskrainfall.betterminecart.mapper.SpringBlocksMapper;
import com.duskrainfall.betterminecart.tools.DataSaver;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.apache.ibatis.session.SqlSession;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Springs {
    private final static HashSet<Location> los = new HashSet<>();
    private final static int CREATE_RADIUS = 5;
    private final static int REMOVE_RADIUS = 10;
    //    private final static long CONTINUE_TIME = 60000L;
    private final static Plugin plugin = JavaPlugin.getPlugin(BetterMinecart.class);

    public final static ConcurrentHashMap<Player, Integer> cds = new ConcurrentHashMap<>();
    private final static int CD = 40;

    public final static String CREATE = "spring.create";
    public final static String REMOVE = "spring.remove";

    public static boolean isCooling(Player player, String opName){
        if(cds.containsKey(player)){
            int ticks = player.getTicksLived() - cds.get(player);
            if(ticks < CD){
                player.sendActionBar(Component.text(opName + "还有 " + (CD - ticks) + " 刻冷却时间！", NamedTextColor.DARK_RED));
                return true;
            }
        }
        cds.put(player, player.getTicksLived());
        return false;
    }

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
//                                livingEntity.addPotionEffect(new PotionEffect(
//                                        PotionEffectType.NAUSEA,
//                                        40, 0, true, false, false
//                                ));
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
    public static void readBlocks(String saveType){
        switch (saveType){
            case "null": return;
            case "data":
                File file = new File(BetterMinecart.PLUGIN_PATH + '/' + BetterMinecart.PLUGIN_DAT);
                if(!(file.exists() && file.length() > 0)) return;

                Path path = Path.of(BetterMinecart.PLUGIN_PATH + '/' + BetterMinecart.PLUGIN_DAT);

                try (ObjectInputStream inputStream = new ObjectInputStream(
                        Files.newInputStream(path)
                )) {
                    DataSaver.addToSet((ArrayList<SpringBlock_Data>) inputStream.readObject(), los);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
                break;
            case "mysql":
                try(SqlSession session = BetterMinecart.sqlSessionFactory.openSession(true)){
                    SpringBlocksMapper springBlocksMapper = session.getMapper(SpringBlocksMapper.class);
                    for(var block : springBlocksMapper.getBlocks()){
                        los.add(DataSaver.toLocation(block));
                    }
                }
                break;
        }
    }

    public static boolean changed = false;
    public static void writeBlocks(String saveType){
        if(!changed) return;
        switch(saveType){
            case "null": return;
            case "data":
                Path path = Path.of(BetterMinecart.PLUGIN_PATH + '/' + BetterMinecart.PLUGIN_DAT);
                DataSaver.touch(path);

                try (ObjectOutputStream outputStream = new ObjectOutputStream(
                        Files.newOutputStream(path)
                )) {
                    outputStream.writeObject(DataSaver.toList(los));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                break;
            case "mysql":
                try(SqlSession session = BetterMinecart.sqlSessionFactory.openSession(true)){
                    SpringBlocksMapper springBlocksMapper = session.getMapper(SpringBlocksMapper.class);
                    springBlocksMapper.clear();
                    for(var lo : los){
                        springBlocksMapper.insertBlock(DataSaver.toSpringBlock_Table(lo));
                    }
                }
                break;
        }
    }
}
