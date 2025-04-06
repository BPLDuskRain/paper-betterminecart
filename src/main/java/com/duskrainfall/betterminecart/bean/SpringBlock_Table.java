package com.duskrainfall.betterminecart.bean;

import com.duskrainfall.betterminecart.annotation.Col;
import com.duskrainfall.betterminecart.annotation.Table;

@Table("tableName_springBlock")
public record SpringBlock_Table(
   @Col(name = "id", type = "bigint", constraint = "PRIMARY KEY")
   long id,
   @Col(name = "world", type = "varchar(20)", constraint = "NOT NULL")
   String world,
   @Col(name = "x", type = "int", constraint = "NOT NULL")
   int x,
   @Col(name = "y", type = "int", constraint = "NOT NULL")
   int y,
   @Col(name = "z", type = "int", constraint = "NOT NULL")
   int z
) {}
