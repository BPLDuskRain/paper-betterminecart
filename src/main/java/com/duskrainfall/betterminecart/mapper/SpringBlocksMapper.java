package com.duskrainfall.betterminecart.mapper;

import com.duskrainfall.betterminecart.bean.SpringBlock_Table;

import java.util.HashSet;

public interface SpringBlocksMapper {
    HashSet<SpringBlock_Table> getBlocks();
    void insertBlock(SpringBlock_Table block);
    void clear();
}
