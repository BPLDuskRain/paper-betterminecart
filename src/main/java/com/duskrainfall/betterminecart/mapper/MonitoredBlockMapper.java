package com.duskrainfall.betterminecart.mapper;

import com.duskrainfall.betterminecart.bean.MonitoredBlock;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MonitoredBlockMapper {
    List<MonitoredBlock> getBlockInfo(@Param("world") String world,
                                      @Param("x") int x,
                                      @Param("y") int y,
                                      @Param("z") int z);
    void insertBlock(MonitoredBlock block);
    void clear();
}
