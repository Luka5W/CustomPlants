package com.luka5w.customplants.common.items;

import com.luka5w.customplants.common.data.ConfigException;
import com.luka5w.customplants.common.data.Plant;
import net.minecraft.item.Item;

public class ItemCustomSeeds extends ItemCustom {
    
    public ItemCustomSeeds(Plant plant) throws ConfigException {
        super(plant.getName() + ".seeds");
    }
}
