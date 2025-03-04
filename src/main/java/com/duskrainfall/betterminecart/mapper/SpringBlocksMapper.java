package com.duskrainfall.betterminecart.mapper;

import com.duskrainfall.betterminecart.records.SpringBlock;

import java.util.HashSet;

public interface SpringBlocksMapper {
    HashSet<SpringBlock> getBlocks();
    void insertBlock(SpringBlock block);
    void clear();
}
