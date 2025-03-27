package com.duskrainfall.betterminecart;

import com.duskrainfall.betterminecart.bean.SpringBlock_Table;
import com.duskrainfall.betterminecart.spring.DropItemListener;
import com.duskrainfall.betterminecart.spring.Springs;
import com.duskrainfall.betterminecart.tools.FileGenerator;
import com.duskrainfall.betterminecart.tools.PropertiesEditor;
import com.duskrainfall.betterminecart.tools.SqlGenerator;
import com.duskrainfall.betterminecart.vehicle.*;
import com.duskrainfall.betterminecart.vehicle.CollisionListener;
import com.duskrainfall.betterminecart.vehicle.DriveListener;
import com.duskrainfall.betterminecart.vehicle.boat.BoatMoveListener;
import com.duskrainfall.betterminecart.vehicle.minecart.MinecartMoveListener;
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
    public final static String PLUGIN_NAME = "betterminecart";
    public final static String PLUGIN_PATH = "plugins/" + PLUGIN_NAME;

    public final static String PLUGIN_PROPERTIES = PLUGIN_NAME + ".properties";
    public final static String PLUGIN_DAT = PLUGIN_NAME + ".dat";
    public final static String PLUGIN_MYBATIS = PLUGIN_NAME + "-mybatis-config.xml";

    private static boolean data = false;
    private static boolean mysql = false;

    public static SqlSessionFactory sqlSessionFactory;

    @Override
    public void onEnable() {
        // Plugin startup logic
        FileGenerator.init();
        PropertiesEditor.read();
        switch(PropertiesEditor.getSaveType()){
            case "null":
                break;
            case "data":
                data = true;
                break;
            case "mysql":
                try {
                    sqlSessionFactory = new SqlSessionFactoryBuilder()
                            .build(new FileInputStream(PLUGIN_PATH + '/' + PLUGIN_MYBATIS));
                    try (SqlSession session = sqlSessionFactory.openSession(true)) {
                        String sql = SqlGenerator.generateCreateTableSQL(SpringBlock_Table.class, session);
                        try(Statement statement = session.getConnection().createStatement()){
                            statement.execute(sql);
                        }catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                mysql = true;
                break;
        }


        PluginCommand vehicleCommand = Bukkit.getPluginCommand("vehicle");
        vehicleCommand.setExecutor(new VehicleCommand());
        vehicleCommand.setTabCompleter(new VehicleTabCompleter());

        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new MinecartMoveListener(), this);
        pluginManager.registerEvents(new BoatMoveListener(), this);
        pluginManager.registerEvents(new DriveListener(), this);
        pluginManager.registerEvents(new CollisionListener(), this);
        pluginManager.registerEvents(new KillEntityListener(), this);

        pluginManager.registerEvents(new DropItemListener(), this);

        if(data || mysql){
            Springs.readBlocks(PropertiesEditor.getSaveType());
            getLogger().log(Level.INFO, "温泉方块已读取");
        }else{
            getLogger().log(Level.INFO, "温泉持久化未开启：未读取温泉方块");
        }
        Springs.springEffect();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        if(data || mysql){
            for(int i = 0; i < Springs.tasks.size(); ++i){
                var task = Springs.tasks.get(i);
                if(task != null) {
                    task.run();
                }
            }
            Springs.writeBlocks(PropertiesEditor.getSaveType());
            getLogger().log(Level.INFO, "温泉方块已保存");
        }
        else{
            getLogger().log(Level.INFO, "温泉持久化未开启：未保存温泉方块");
        }
    }
}
