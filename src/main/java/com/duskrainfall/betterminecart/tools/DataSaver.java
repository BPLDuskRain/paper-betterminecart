package com.duskrainfall.betterminecart.tools;

import com.duskrainfall.betterminecart.bean.SpringBlock_Data;
import com.duskrainfall.betterminecart.bean.SpringBlock_Table;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;

public class DataSaver {
    public static void touch(Path path){
        if(!Files.exists(path)){
            try {
                Files.createFile(path);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    public static Location toLocation(SpringBlock_Data springBlock){
        return new Location(
                Bukkit.getWorld(springBlock.world),
                springBlock.x,
                springBlock.y,
                springBlock.z
        );
    }
    public static Location toLocation(SpringBlock_Table springBlock){
        return new Location(
                Bukkit.getWorld(springBlock.world()),
                springBlock.x(),
                springBlock.y(),
                springBlock.z()
        );
    }

    public static SpringBlock_Data toSpringBlock_Data(Location location){
        return new SpringBlock_Data(
                location.getWorld().getName(),
                location.getX(),
                location.getY(),
                location.getZ()
        );
    }
    private static long count= 1;
    public static SpringBlock_Table toSpringBlock_Table(Location location){
        return new SpringBlock_Table(
                count++,
                location.getWorld().getName(),
                location.getX(),
                location.getY(),
                location.getZ()
        );
    }

    public static ArrayList<SpringBlock_Data> toList(HashSet<Location> los){
        ArrayList<SpringBlock_Data> arrayList = new ArrayList<>();
        for(var lo : los){
            arrayList.add(toSpringBlock_Data(lo));
        }
        return arrayList;
    }

    public static void addToSet(ArrayList<SpringBlock_Data> list, HashSet<Location> los){
        for(var block : list){
            los.add(toLocation(block));
        }
    }
}
