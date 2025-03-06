package com.duskrainfall.betterminecart.tools;

import com.duskrainfall.betterminecart.BetterMinecart;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileGenerator {
    public final static String MYBATIS_CONFIG_TEMPLATE = """
            <?xml version="1.0" encoding="UTF-8" ?>
            <!DOCTYPE configuration
                    PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
                    "http://mybatis.org/dtd/mybatis-3-config.dtd">
            <configuration>
                <properties>
                    <property name="tableName" value="表名"/>
                </properties>
                <!-- 配置环境 -->
                <environments default="development">
                    <environment id="development">
                        <transactionManager type="JDBC"/>
                        <dataSource type="POOLED">
                            <property name="driver" value="com.mysql.cj.jdbc.Driver"/>
                            <property name="url" value="jdbc:mysql://数据库主机:数据库端口/数据库名?useSSL=false&amp;serverTimezone=UTC"/>
                            <property name="username" value="用户名"/>
                            <property name="password" value="密码"/>
                        </dataSource>
                        
                    </environment>
                </environments>
                        
                <!-- 加载映射文件 -->
                <mappers>
                    <mapper resource="mapper/SpringBlocksMapper.xml"/>
                </mappers>
                        
            </configuration>
            """;
    public static void mkdir(String pathString){
        Path path = Path.of(pathString);
        if(!Files.exists(path)){
            try {
                Files.createDirectory(path);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static void touch_properties(String pathString){
        Path path = Path.of(pathString);
        if(!Files.exists(path)){
            try {
                Files.createFile(path);
                PropertiesEditor.initProperties();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static void touch_mybatis(String pathString){
        Path path = Path.of(pathString);
        if(!Files.exists(path)){
            try {
                Files.createFile(path);
                mybatisConfigGenerate(pathString);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    private static void mybatisConfigGenerate(String path){
        try (FileOutputStream fileOutputStream = new FileOutputStream(path)) {
            fileOutputStream.write(MYBATIS_CONFIG_TEMPLATE.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void init(){
        mkdir(BetterMinecart.PLUGIN_PATH);
        touch_properties(BetterMinecart.PLUGIN_PATH + '/' + BetterMinecart.PLUGIN_PROPERTIES);
        touch_mybatis(BetterMinecart.PLUGIN_PATH + '/' + BetterMinecart.PLUGIN_MYBATIS);
    }
}
