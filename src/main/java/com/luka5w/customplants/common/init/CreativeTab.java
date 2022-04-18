package com.luka5w.customplants.common.init;

import com.luka5w.customplants.CustomPlants;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

public class CreativeTab extends net.minecraft.creativetab.CreativeTabs {
    
    private static CreativeTab instance;
    
    public CreativeTab(String label) {
        super(CustomPlants.MOD_ID);
        instance = this;
    }
    
    @Override
    public ItemStack getTabIconItem() {
        return new ItemStack(Blocks.SAPLING);
    }
    
    public static CreativeTab getInstance() {
        return instance;
    }
}
