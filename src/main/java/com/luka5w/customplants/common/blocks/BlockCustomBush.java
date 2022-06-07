package com.luka5w.customplants.common.blocks;

import com.luka5w.customplants.common.plantsbehavior.PlantBehavior;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class BlockCustomBush extends BlockCustomPlant {
    
    public static final PropertyBool ACTIVATED = PropertyBool.create("activated");
    public static final PropertyEnum<EnumFacing> FACING = PropertyEnum.create("facing", EnumFacing.class);
    
    public BlockCustomBush(AxisAlignedBB[] aabbs, PlantBehavior behavior, Material blockMaterial, BlockFaceShape blockShape,
                           @Nullable List<Tuple<Float, ItemStack>> drops, List<EnumFacing> facings, String oreDict, EnumPlantType type,
                           @Nullable ArrayList<String> soilsList, boolean soilsAllowed) {
        super(blockMaterial, aabbs, blockShape, behavior, drops, facings, soilsList, soilsAllowed, type);
    }
    
    // PLACEMENT
    
    @Override
    public boolean canPlaceBlockOnSide(World worldIn, BlockPos pos, EnumFacing side) {
        return this.facings.contains(side) &&
               this.canBlockStay(worldIn, pos, this.getDefaultState().withProperty(FACING, side)) &&
               (this.soilsList != null || super.canPlaceBlockAt(worldIn, pos));
    }
    
    @Override
    public IBlockState withRotation(IBlockState state, Rotation rot) {
        return state.withProperty(FACING, rot.rotate(state.getValue(FACING)));
    }
    
    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY,
                                            float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        return this.getDefaultState().withProperty(ACTIVATED, false)
                   .withProperty(FACING, facing);
    }
    
    @Override
    public IBlockState withMirror(IBlockState state, Mirror mirrorIn) {
        return state.withProperty(FACING, mirrorIn.mirror(state.getValue(FACING)));
    }
    
    // TICKS/ UPDATES, TE
    
    // TICKS/ EVENTS
    
    // PROPERTIES
    
    @Override
    protected AxisAlignedBB getAABB(IBlockState state) {
        return this.aabbs[this.getFacing(state).ordinal()];
    }
    
    @Override
    protected IBlockState setActivated(boolean activatedOut, IBlockState stateIn) {
        return super.setActivated(activatedOut, stateIn)
                    .withProperty(FACING, this.getFacing(stateIn));
    }
    
    @Override
    protected boolean isActivated(IBlockState state) {
        return state.getValue(ACTIVATED);
    }
    
    protected IBlockState setFacing(IBlockState stateIn) {
        return super.setActivated(stateIn.getValue(ACTIVATED), stateIn)
                    .withProperty(FACING, this.getFacing(stateIn));
    }
    
    @Override
    protected EnumFacing getFacing(IBlockState state) {
        return state.getValue(FACING);
    }
    
    // STATES, PLANT TYPE
    
    @Override
    protected IBlockState createDefaultBlockState(IBlockState stateIn) {
        return super.createDefaultBlockState(stateIn)
                    .withProperty(FACING, this.facings.get(0));
    }
    
    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING, ACTIVATED);
    }
    
    @Override
    public IBlockState getStateFromMeta(int meta) {
        boolean active = (meta & 1) == 1;
        int facing = (meta >> 1) & 7;
        if (facing >= EnumFacing.values().length) return null;
        return this.getDefaultState()
                   .withProperty(ACTIVATED, active)
                   .withProperty(FACING, EnumFacing.values()[facing]);
    }
    
    @Override
    public int getMetaFromState(IBlockState state) {
        int i = this.isActivated(state) ? 1 : 0;
        i = i | (this.getFacing(state).ordinal() << 1);
        return i;
    }

    
}
