package com.duskrainfall.betterminecart.bean;

import java.io.Serializable;

public class SpringBlock_Data implements Serializable {
    public String world;
    public double x;
    public double y;
    public double z;

    public SpringBlock_Data(String world, double x, double y, double z) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
    }
}
