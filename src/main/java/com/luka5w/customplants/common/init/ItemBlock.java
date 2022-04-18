package com.luka5w.customplants.common.init;

import com.luka5w.customplants.CustomPlants;
import com.luka5w.customplants.common.blocks.BlockCustomPlant;
import net.minecraft.item.ItemStack;

/**
 * Fixes missing translations by using the internal translation tool.
 */
public class ItemBlock extends net.minecraft.item.ItemBlock {
    
    public ItemBlock(BlockCustomPlant plant, CreativeTab tab) {
        this(plant);
        setCreativeTab(tab);
    }
    public ItemBlock(BlockCustomPlant plant) {
        super(plant);
        setRegistryName(plant.getRegistryName());
        setUnlocalizedName(plant.getUnlocalizedName());
    }
    
    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        return CustomPlants.getProxy().getTranslation(this.getUnlocalizedNameInefficiently(stack) + ".name").trim();
    }
}
