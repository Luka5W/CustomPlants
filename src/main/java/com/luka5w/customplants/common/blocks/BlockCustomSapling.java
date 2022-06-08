package com.luka5w.customplants.common.blocks;

import com.luka5w.customplants.common.plantsbehavior.PlantBehavior;
import com.luka5w.customplants.common.util.debug_please_remove.DebugUtils;
import net.minecraft.block.IGrowable;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.*;
import net.minecraftforge.common.EnumPlantType;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BlockCustomSapling extends BlockCustomBush implements IGrowable {
    public static final PropertyEnum<EnumFacing> FACING = PropertyEnum.create("facing", EnumFacing.class);
    public static final PropertyInteger STAGE = PropertyInteger.create("stage", 0, 1);
    private final boolean canUseBonemeal;
    private final int reqLightLvl;
    private final List<WorldGenerator[]> treeGen;
    
    
    public BlockCustomSapling(AxisAlignedBB[] aabbs, PlantBehavior behavior, Material blockMaterial, BlockFaceShape blockShape,
                              @Nullable List<Tuple<Float, ItemStack>> drops, List<EnumFacing> facings, EnumPlantType type,
                              boolean canUseBonemeal, int reqLightLvl, List<WorldGenerator[]> treeGen,
                              @Nullable ArrayList<String> soilsList, boolean soilsAllowed) {
        super(aabbs, behavior, blockMaterial, blockShape, drops, facings, type, soilsList, soilsAllowed);
        this.canUseBonemeal = canUseBonemeal;
        this.reqLightLvl = reqLightLvl;
        this.treeGen = treeGen;
    }
    
    // PLACEMENT
    
    // TICKS/ UPDATES, TE
    
    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        if (!worldIn.isRemote) {
            super.updateTick(worldIn, pos, state, rand);
            if (!worldIn.isAreaLoaded(pos, 1)) return; // Forge: prevent loading unloaded chunks when checking neighbor's light
            if (worldIn.getLightFromNeighbors(this.getBlockPosToGrowing(state, pos)) >= this.reqLightLvl && rand.nextInt(7) == 0) {
                this.grow(worldIn, rand, pos, state);
            }
        }
    }
    
    @Override
    public boolean canGrow(World worldIn, BlockPos pos, IBlockState state, boolean isClient) {
        return true;
    }
    
    @Override
    public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, IBlockState state) {
        return this.canUseBonemeal && (double)worldIn.rand.nextFloat() < 0.45D;
    }
    
    @Override
    public void grow(World worldIn, Random rand, BlockPos pos, IBlockState state) {
        if (state.getValue(STAGE) == 0) {
            worldIn.setBlockState(pos, state.cycleProperty(STAGE), 4);
        }
        else {
            this.generateTree(worldIn, pos, state, rand);
        }
    }
    
    public void generateTree(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        if (this.treeGen.isEmpty() || !net.minecraftforge.event.terraingen.TerrainGen.saplingGrowTree(worldIn, rand, pos)) return;
        
        // remove sapling
        worldIn.setBlockState(pos, Blocks.AIR.getDefaultState(), 4);
        
        // try gen tree, if it fails, add the removed sapling again
        if (!this.treeGen.get(rand.nextInt(this.treeGen.size()))[this.getFacing(state).ordinal()].generate(worldIn, rand, pos)) {
            worldIn.setBlockState(pos, state, 4);
        }
    }
    
    // TICKS/ EVENTS
    
    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
                                    EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        DebugUtils.releaseMouse(worldIn, true);
        return false;
    }
    
    // PROPERTIES
    
    @Override
    protected boolean isActivated(IBlockState state) {
        return false;
    }
    
    @Override
    protected IBlockState setActivated(boolean activatedOut, IBlockState stateIn) {
        return stateIn;
    }
    
    // STATES, PLANT TYPE
    
    @Override
    protected IBlockState createDefaultBlockState(IBlockState stateIn) {
        return stateIn.withProperty(FACING, this.facings.get(0))
                      .withProperty(STAGE, 0);
    }
    
    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING, STAGE);
    }
    
    @Override
    public IBlockState getStateFromMeta(int meta) {
        int stage = (meta & 1);
        int facing = (meta >> 1) & 7;
        if (facing >= EnumFacing.values().length) return null;
        return this.getDefaultState()
                   .withProperty(FACING, EnumFacing.values()[facing])
                   .withProperty(STAGE, stage);
    }
    
    @Override
    public int getMetaFromState(IBlockState state) {
        int i = state.getValue(STAGE);
        i = i | (this.getFacing(state).ordinal() << 1);
        return i;
    }
}
