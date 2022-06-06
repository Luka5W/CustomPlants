package com.luka5w.customplants.common.data.plantspacks.plants;

import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.common.EnumPlantType;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * A holder for the parsed #plant section of plant config files.
 * @see com.luka5w.customplants.common.data.plantspacks.plants.serialization.v0.PlantSerializer
 */
public class Plant {
    private final AxisAlignedBB[] aabbs;
    private final int burnTime;
    private final List<Tuple<Float, ItemStack>> drops;
    private final List<EnumFacing> facings;
    private final Material material;
    private final String oreDict;
    private final EnumPlantType type;
    private final ArrayList<String> soils;
    private final boolean soilsAllowed;
    
    public Plant(AxisAlignedBB[] aabbs, int burnTime, List<Tuple<Float, ItemStack>> drops, List<EnumFacing> facings,
                 Material material, String oreDict, EnumPlantType type, @Nullable ArrayList<String> soils,
                 boolean soilsAllowed) {
        this.aabbs = aabbs;
        this.burnTime = burnTime;
        this.drops = drops;
        this.facings = facings;
        this.material = material;
        this.oreDict = oreDict;
        this.type = type;
        this.soils = soils;
        this.soilsAllowed = soilsAllowed;
    }
    
    public AxisAlignedBB[] getAABBs() {
        return aabbs;
    }
    
    public int getBurnTime() {
        return burnTime;
    }
    
    public List<Tuple<Float, ItemStack>> getDrops() {
        return drops;
    }
    
    public List<EnumFacing> getFacings() {
        return facings;
    }
    
    public Material getMaterial() {
        return material;
    }
    
    public String getOreDict() {
        return oreDict;
    }
    
    public EnumPlantType getType() {
        return type;
    }
    
    @Nullable
    public ArrayList<String> getSoils() {
        return soils;
    }
    
    public boolean isSoilsAllowed() {
        return soilsAllowed;
    }
    
    public boolean isSoilsEnabled() {
        return soils != null;
    }
}
