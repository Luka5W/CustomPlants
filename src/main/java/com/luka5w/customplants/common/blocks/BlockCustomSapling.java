package com.luka5w.customplants.common.blocks;

import com.luka5w.customplants.common.data.ConfigException;
import com.luka5w.customplants.common.data.Plant;

public class BlockCustomSapling extends BlockCustomPlant {
    public BlockCustomSapling(Plant plant) throws ConfigException {
        super(plant.getName() + ".sapling");
    }
}
