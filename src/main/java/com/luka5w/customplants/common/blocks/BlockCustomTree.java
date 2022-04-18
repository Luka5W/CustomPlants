package com.luka5w.customplants.common.blocks;

import com.luka5w.customplants.common.data.ConfigException;
import com.luka5w.customplants.common.data.Plant;

public class BlockCustomTree extends BlockCustomPlant {
    
    public BlockCustomTree(Plant plant) throws ConfigException {
        super(plant.getName());
    }
}
