package com.duskrainfall.betterminecart.tools;

import com.duskrainfall.betterminecart.annotation.Col;
import com.duskrainfall.betterminecart.annotation.Table;
import org.apache.ibatis.session.SqlSession;

import java.lang.reflect.Field;

public class SqlGenerator {
    private static String getTableName(Class<?> clazz, SqlSession session){
        if(!clazz.isAnnotationPresent(Table.class))
            throw new IllegalArgumentException("此类未标注@Table");

        Table table = clazz.getAnnotation(Table.class);
        return session.getConfiguration()
                .getVariables()
                .getProperty(table.value());
    }
    public static String generateCreateTableSQL(Class<?> clazz, SqlSession session){
        if(!clazz.isAnnotationPresent(Table.class))
            throw new IllegalArgumentException("此类未标注@Table");

        StringBuilder sql = new StringBuilder();
        sql.append(" CREATE TABLE IF NOT EXISTS ").append(getTableName(clazz, session));
        sql.append("(\n");

        for(Field field : clazz.getDeclaredFields()){
            if(field.isAnnotationPresent(Col.class)){
                Col col = field.getAnnotation(Col.class);
                sql.append(col.name()).append(" ").append(col.type()).append(" ").append(col.constraint()).append(",\n");
            }
        }
        sql.deleteCharAt(sql.length() - 2);
        sql.append(");");
        return sql.toString();
    }
}
