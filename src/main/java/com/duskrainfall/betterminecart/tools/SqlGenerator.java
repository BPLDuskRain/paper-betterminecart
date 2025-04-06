package com.duskrainfall.betterminecart.tools;

import com.duskrainfall.betterminecart.annotation.Col;
import com.duskrainfall.betterminecart.annotation.Table;
import org.apache.ibatis.session.SqlSession;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;

public class SqlGenerator {
    public static String getCreateDatabaseSQL(){
        return "CREATE DATABASE IF NOT EXISTS better_minecart;";
    }

    public static String changeDatabase(){
        return "USE better_minecart;";
    }

    private static String getTableName(Class<?> clazz, SqlSession session){
        Table table = clazz.getAnnotation(Table.class);
        return session.getConfiguration()
                .getVariables()
                .getProperty(table.value());
    }

    public static String getCreateTableSQL(Class<?> clazz, SqlSession session){
        StringBuilder sql = new StringBuilder();
        sql.append(" CREATE TABLE IF NOT EXISTS ").append(getTableName(clazz, session));
        sql.append("(\n");

        for(Field field : clazz.getDeclaredFields()){
            if(field.isAnnotationPresent(Col.class)){
                Col col = field.getAnnotation(Col.class);
                sql.append(col.name()).append(" ").append(col.type()).append(" ").append(col.constraint()).append(",\n");
            }
        }
        for(String s : getCreateIndexSQL(clazz)){
            sql.append(s);
        }
        sql.deleteCharAt(sql.length() - 2);
        sql.append(");");
        return sql.toString();
    }

    public static ArrayList<String> getCreateIndexSQL(Class<?> clazz){
        ArrayList<String> list = new ArrayList<>();
        HashSet<String> set = new HashSet<>();
        for(Field field : clazz.getDeclaredFields()){
            if(field.isAnnotationPresent(Col.class)){
                Col col = field.getAnnotation(Col.class);
                if(!col.index().isEmpty()) set.add(col.index());
            }
        }
        for(var index : set){
            StringBuilder sql = new StringBuilder();
            sql.append("INDEX ").append(index).append(" ").append("(");
            for(Field field : clazz.getDeclaredFields()){
                if(field.isAnnotationPresent(Col.class)){
                    Col col = field.getAnnotation(Col.class);
                    String colIndex = col.index();
                    if(colIndex.equals(index)){
                        sql.append(col.name()).append(",");
                    }
                }
            }
            sql.deleteCharAt(sql.length() - 1);
            sql.append("),\n");
            list.add(sql.toString());
        }
        return list;
    }
}
