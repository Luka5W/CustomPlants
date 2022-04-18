package com.luka5w.customplants.common.blocks;

import com.luka5w.customplants.CustomPlants;
import com.luka5w.customplants.common.data.ConfigException;
import com.luka5w.customplants.old.common.init.CreativeTab;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public abstract class BlockCustomPlant extends Block {
    
    public BlockCustomPlant(String name) throws ConfigException {
        super(Material.PLANTS);
        setRegistryName(name);
        setUnlocalizedName(name);
        setCreativeTab(CreativeTab.getInstance());
    }
    
    @Override
    public String getLocalizedName() {
        return CustomPlants.getProxy().getTranslation(this.getUnlocalizedName() + ".name");
    }
}
