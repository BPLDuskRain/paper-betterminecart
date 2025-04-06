package com.duskrainfall.betterminecart.bean;

import com.duskrainfall.betterminecart.annotation.Col;
import com.duskrainfall.betterminecart.annotation.Table;

import java.time.LocalDateTime;

@Table("tableName_monitoredBlock")
public record MonitoredBlock(
    @Col(name = "id", type = "bigint", constraint = "AUTO_INCREMENT PRIMARY KEY")
    long id,
    @Col(name = "block", type = "varchar(50)", constraint = "NOT NULL")
    String block,
    @Col(name = "world", type = "varchar(20)", constraint = "NOT NULL", index = "index_block")
    String world,
    @Col(name = "x", type = "int", constraint = "NOT NULL", index = "index_block")
    int x,
    @Col(name = "y", type = "int", constraint = "NOT NULL", index = "index_block")
    int y,
    @Col(name = "z", type = "int", constraint = "NOT NULL", index = "index_block")
    int z,
    @Col(name = "player", type = "varchar(20)", constraint = "NOT NULL")
    String player,
    @Col(name = "time", type = "datetime", constraint = "NOT NULL")
    LocalDateTime time,
    @Col(name = "action", type = "varchar(10)", constraint = "NOT NULL")
    String action
) {}
