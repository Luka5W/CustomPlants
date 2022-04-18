package com.luka5w.customplants.common.items;

import com.luka5w.customplants.CustomPlants;
import com.luka5w.customplants.common.data.ConfigException;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public abstract class ItemCustom extends Item {
    public ItemCustom(String name) throws ConfigException {
        super();
        setRegistryName(name);
        setUnlocalizedName(name);
    }
    
    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        return CustomPlants.getProxy().getTranslation(this.getUnlocalizedNameInefficiently(stack) + ".name").trim();
    }
}
