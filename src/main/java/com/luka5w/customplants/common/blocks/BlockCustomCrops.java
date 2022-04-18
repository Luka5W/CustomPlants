package com.luka5w.customplants.common.blocks;

import com.luka5w.customplants.common.data.ConfigException;
import com.luka5w.customplants.common.data.Plant;
import net.minecraft.block.IGrowable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;

import java.util.Random;

public class BlockCustomCrops extends BlockCustomPlant implements IGrowable, IPlantable {
    
    private final boolean allowPickup;
    
    public BlockCustomCrops(Plant plant) throws ConfigException {
        super(plant.getName());
        setTickRandomly(true);
        this.allowPickup = plant.isPickupAllowed();
        String[] textures = plant.getTextures();
        
    }
    
    public boolean isPickupAllowed() {
        return this.allowPickup;
    }
    
    @Override
    public boolean canGrow(World worldIn, BlockPos pos, IBlockState state, boolean isClient) {
        return false;
    }
    
    @Override
    public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, IBlockState state) {
        return false;
    }
    
    @Override
    public void grow(World worldIn, Random rand, BlockPos pos, IBlockState state) {
    
    }
    
    @Override
    public EnumPlantType getPlantType(IBlockAccess world, BlockPos pos) {
        return null;
    }
    
    @Override
    public IBlockState getPlant(IBlockAccess world, BlockPos pos) {
        return null;
    }
}
