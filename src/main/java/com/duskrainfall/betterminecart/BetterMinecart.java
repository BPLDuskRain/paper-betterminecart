package com.duskrainfall.betterminecart;

import com.duskrainfall.betterminecart.records.SpringBlock;
import com.duskrainfall.betterminecart.spring.DropItemListener;
import com.duskrainfall.betterminecart.spring.Springs;
import com.duskrainfall.betterminecart.tools.SqlGenerator;
import com.duskrainfall.betterminecart.vehicle.*;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;

public final class BetterMinecart extends JavaPlugin {
    public static boolean canSaved = true;
    public static SqlSessionFactory sqlSessionFactory;
    static {
        try {
            sqlSessionFactory = new SqlSessionFactoryBuilder()
                    .build(new FileInputStream("plugins/betterminecart-mybatis-config.xml"));
            try (SqlSession session = sqlSessionFactory.openSession(true)) {
                String sql = SqlGenerator.generateCreateTableSQL(SpringBlock.class, session);
                try(Statement statement = session.getConnection().createStatement()){
                    statement.execute(sql);
                }catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (IOException e) {
            canSaved = false;
        }
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        PluginCommand vehicleCommand = Bukkit.getPluginCommand("vehicle");
        vehicleCommand.setExecutor(new VehicleCommand());
        vehicleCommand.setTabCompleter(new VehicleTabCompleter());

        PluginManager pluginManager =  Bukkit.getPluginManager();
        pluginManager.registerEvents(new VehicleMovementListener(), this);
        pluginManager.registerEvents(new DriveListener(), this);
        pluginManager.registerEvents(new CollisionListener(), this);

        DropItemListener dropItemListener =  new DropItemListener();
        pluginManager.registerEvents(dropItemListener, this);

        if(canSaved){
            Springs.readBlocks();
            getLogger().log(Level.INFO, "温泉方块已读取");
        }else{
            getLogger().log(Level.INFO, "温泉持久化未开启：未读取温泉方块");
        }
        Springs.springEffect();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        if(canSaved){
            for(int i = 0; i < Springs.tasks.size(); ++i){
                var task = Springs.tasks.get(i);
                if(task != null) {
                    task.run();
                }
            }
            Springs.writeBlocks();
            getLogger().log(Level.INFO, "温泉方块已保存");
        }
        else{
            getLogger().log(Level.INFO, "温泉持久化未开启：未保存温泉方块");
        }
    }
}
