package com.duskrainfall.betterminecart.bean;

import com.duskrainfall.betterminecart.annotation.Col;
import com.duskrainfall.betterminecart.annotation.Table;

@Table("tableName")
public record SpringBlock_Table(
   @Col(name = "id", type = "bigint", constraint = "PRIMARY KEY")
   long id,
   @Col(name = "world", type = "varchar(20)", constraint = "NOT NULL")
   String world,
   @Col(name = "x", type = "double", constraint = "NOT NULL")
   double x,
   @Col(name = "y", type = "double", constraint = "NOT NULL")
   double y,
   @Col(name = "z", type = "double", constraint = "NOT NULL")
   double z
) {}
