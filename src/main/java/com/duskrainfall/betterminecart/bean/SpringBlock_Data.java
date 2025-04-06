package com.duskrainfall.betterminecart.bean;

import java.io.Serializable;

public class SpringBlock_Data implements Serializable {
    public String world;
    public int x;
    public int y;
    public int z;

    public SpringBlock_Data(String world, int x, int y, int z) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
    }
}
