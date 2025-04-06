package com.duskrainfall.betterminecart.tools;

import com.duskrainfall.betterminecart.BetterMinecart;
import com.duskrainfall.betterminecart.bean.MonitoredBlock;
import com.duskrainfall.betterminecart.mapper.MonitoredBlockMapper;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class Monitor implements Listener, CommandExecutor {
    private final static String MONITOR_CLEAR = "monitor.clear";
    private final static ConcurrentHashMap<Player, Boolean> map = new ConcurrentHashMap<>();

    private final static int COMMIT_CD = 10;
    private final static AtomicInteger atomicCount = new AtomicInteger(0);
    private final static List<MonitoredBlock> list = new ArrayList<>();

    private static void sessionClear(){
        try(SqlSession session = BetterMinecart.sqlSessionFactory.openSession(true)){
            session.getMapper(MonitoredBlockMapper.class).clear();
        }
    }

    private static void sessionTryCommit(){
        int count = atomicCount.incrementAndGet();

        if(count % COMMIT_CD == 0){
            List<MonitoredBlock> commitList;
            synchronized (list){
                commitList = new ArrayList<>(list);
            }
            try(SqlSession session = BetterMinecart.sqlSessionFactory.openSession(ExecutorType.BATCH)){
                var mapper = session.getMapper(MonitoredBlockMapper.class);
                    for(var block : commitList){
                        mapper.insertBlock(block);
                    }
                session.commit();
            }
            synchronized (list){
                list.removeAll(commitList);
            }
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String nickname, @NotNull String[] args) {
        if(!(sender instanceof Player player)) return true;
        if(!BetterMinecart.isMonitoring) {
            player.sendMessage("§c视奸未启用");
            return true;
        }

        if(args.length == 0){
            boolean monitoring = map.getOrDefault(player, false);
            map.put(player, !monitoring);
            if(!monitoring){
                player.sendMessage("§c§l开始视奸方块模式");
            }
            else{
                player.sendMessage("§c§l退出视奸方块模式");
            }
            return true;
        }

        switch (args[0]){
            case "clear" -> {
                if(!player.hasPermission(MONITOR_CLEAR)){
                    player.sendMessage("你不是管理员");
                    return true;
                }
                sessionClear();
                player.sendMessage("方块历史已经清空");
            }
            default -> {
                player.sendMessage("非法的参数");
                return false;
            }
        }

        return true;
    }

    @EventHandler
    public void OnClick(PlayerInteractEvent e){
        if(!BetterMinecart.isMonitoring) return;
        Player player = e.getPlayer();
        boolean monitoring = map.getOrDefault(player, false);
        if(!monitoring) return;

        if (e.getAction().equals(Action.LEFT_CLICK_BLOCK)){
            e.setCancelled(true);
            var block = e.getClickedBlock();
            if(block == null) {
                player.sendMessage("无效方块");
                return;
            }
            var location = block.getLocation();
            List<MonitoredBlock> blocksInfo;
            try(SqlSession session = BetterMinecart.sqlSessionFactory.openSession()){
                blocksInfo = session.getMapper(MonitoredBlockMapper.class).getBlockInfo(
                        location.getWorld().getName(),
                        location.getBlockX(), location.getBlockY(), location.getBlockZ()
                );
            }

            player.sendMessage("§l==At " + location.getWorld().getName()
                    + "(" + location.getBlockX()
                    + ", " + location.getBlockY()
                    + ", " + location.getBlockZ()
                    + ")==");
            for(var blockInfo : blocksInfo){
                player.sendMessage("§e" + blockInfo.time()
                        + " §b" + blockInfo.player()
                        + " §a" + blockInfo.action()
                        + " §b" + blockInfo.block()
                        );
            }

            sessionTryCommit();
        }
    }

    @EventHandler
    public void OnBlockPlace(BlockPlaceEvent e){
        if(!BetterMinecart.isMonitoring) return;

        var block = e.getBlock();
        var player = e.getPlayer();
        var location = block.getLocation();
        list.add(new MonitoredBlock(
                0,
                block.getType().name(),
                location.getWorld().getName(),
                location.getBlockX(),
                location.getBlockY(),
                location.getBlockZ(),
                player.getName(),
                LocalDateTime.now(),
                "PLACE"
        ));

        sessionTryCommit();
    }

    @EventHandler
    public void OnBlockBreak(BlockBreakEvent e){
        if(!BetterMinecart.isMonitoring) return;

        var block = e.getBlock();
        var player = e.getPlayer();
        var location = block.getLocation();
        list.add(new MonitoredBlock(
                0,
                block.getType().name(),
                location.getWorld().getName(),
                location.getBlockX(),
                location.getBlockY(),
                location.getBlockZ(),
                player.getName(),
                LocalDateTime.now(),
                "BREAK"
        ));

        sessionTryCommit();
    }
}