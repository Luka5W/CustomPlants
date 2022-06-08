package com.luka5w.customplants.common.blocks;

import com.luka5w.customplants.common.plantsbehavior.PlantBehavior;
import com.luka5w.customplants.common.util.debug_please_remove.DebugUtils;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class BlockCustomExtendable extends BlockCustomPlant {
    
    public static final int MAX_AGE = 7;
    
    public static final PropertyInteger AGE = PropertyInteger.create("age", 0, MAX_AGE);
    
    public BlockCustomExtendable(AxisAlignedBB[] aabbs, PlantBehavior behavior, Material blockMaterial,
                                 BlockFaceShape blockShape, @Nullable List<Tuple<Float, ItemStack>> drops,
                                 EnumFacing facing, EnumPlantType type,
                                 @Nullable ArrayList<String> soilsList, boolean soilsAllowed) {
        super(blockMaterial, aabbs, blockShape, behavior, drops, Collections.singletonList(facing),
              soilsList, soilsAllowed, type);
        this.setTickRandomly(true);
    }
    
    // PLACEMENT
    
    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        return this.canPlaceBlockOnSide(worldIn, pos, this.facings.get(0));
    }
    
    @Override
    protected boolean canBlockStay(World worldIn, BlockPos pos, IBlockState state) {// TODO CHECK
        BlockPos soilPos = this.getBlockPosToSoil(state, pos);
        IBlockState soil = worldIn.getBlockState(soilPos);
        Block soilBlock = soil.getBlock();
        if (soilBlock == this) return true;
        if (this.soilsList == null) {
            // TODO: 07.06.22 WTF?
            return soilBlock.canSustainPlant(soil, worldIn, soilPos, this.getFacing(state), this);
        }
        else {
            ResourceLocation soilsRes = soilBlock.getRegistryName();
            if (soilsRes == null) return false; // don't allow this plant to be planted on blocks without a name
            // allow this plant to be planted on allowed blocks or on every block which is not denied
            return this.soilsAllowed == (this.soilsList.contains(soilsRes.toString()) ||
                                         this.soilsList.contains(soilsRes + ":" + soil.getBlock().getMetaFromState(soil)));
        }
    }
    
    // TICKS/ UPDATES, TE
    
    @Override
    public boolean hasTileEntity(IBlockState state) {
        return this.behavior.hasFeatureEffects();
    }
    
    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        if (!worldIn.isRemote) {
            super.updateTick(worldIn, pos, state, rand);
            BlockPos soilPos = this.getBlockPosToSoil(state, pos);
            BlockPos growingPos = this.getBlockPosToGrowing(state, pos);
            if (worldIn.getBlockState(soilPos).getBlock() == this || this.checkAndDropBlock(worldIn, pos, state)) {
                if (worldIn.isAirBlock(growingPos)) {
            
                    int blocksBefore;
                    //noinspection StatementWithEmptyBody
                    for (blocksBefore = 1;
                         worldIn.getBlockState(this.getBlockPosToSoil(state, pos, blocksBefore)).getBlock() == this;
                         ++blocksBefore)
                        ;
            
                    if (blocksBefore < 3) {
                        int j = state.getValue(AGE);
                
                        if (net.minecraftforge.common.ForgeHooks.onCropsGrowPre(worldIn, pos, state, true)) {
                            if (j == this.getMaxAge()) {
                                worldIn.setBlockState(growingPos, this.getDefaultState());
                                worldIn.setBlockState(pos, state.withProperty(AGE, 0), 4);
                            }
                            else {
                                worldIn.setBlockState(pos, state.withProperty(AGE, j + 1), 4);
                            }
                            net.minecraftforge.common.ForgeHooks.onCropsGrowPost(worldIn, pos, state,
                                                                                 worldIn.getBlockState(pos));
                        }
                    }
                }
            }
        }
    }
    
    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
                                    EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);
        return true;
    }
    
    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        super.neighborChanged(state, worldIn, pos, blockIn, fromPos);
        DebugUtils.releaseMouse(worldIn, true);
        if (blockIn == this &&
            worldIn.getBlockState(fromPos).getBlock() == this) {
            BlockPos maybeFromPos1 = this.getBlockPosToSoil(state, pos);
            BlockPos maybeFromPos2 = this.getBlockPosToGrowing(state, pos);
            boolean isThisActivated = this.isActivated(state);
            if ((fromPos.equals(maybeFromPos1) &&
                 this.isActivated(worldIn.getBlockState(maybeFromPos1)) != isThisActivated) ||
                (fromPos.equals(maybeFromPos2) &&
                 this.isActivated(worldIn.getBlockState(maybeFromPos2)) != isThisActivated)) {
                this.toggleActivation(worldIn, pos, state);
            }
        }
    }
    
    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ,
                                            int meta, EntityLivingBase placer, EnumHand hand) {
        IBlockState state = super.getStateForPlacement(world, pos, facing, hitX, hitY, hitZ, meta, placer, hand);
        IBlockState soilState = world.getBlockState(this.getBlockPosToSoil(state, pos));
        return soilState.getBlock() == this ?
                state.withProperty(ACTIVATED, this.isActivated(soilState)) :
                state;
    }
    
    @Override
    protected AxisAlignedBB getAABB(IBlockState state) {
        return this.aabbs[this.getAge(state)];
    }
    
    @Override
    protected IBlockState setActivated(boolean activatedOut, IBlockState stateIn) {
        return super.setActivated(activatedOut, stateIn)
                    .withProperty(AGE, this.getAge(stateIn));
    }
    
    @Override
    protected EnumFacing getFacing(IBlockState state) {
        return this.facings.get(0);
    }
    
    protected int getMaxAge() {
        return Math.min(MAX_AGE, this.aabbs.length);
    }
    
    @Override
    protected IBlockState createDefaultBlockState(IBlockState stateIn) {
        return super.createDefaultBlockState(stateIn)
                    .withProperty(AGE, 0);
    }
    
    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, ACTIVATED, AGE);
    }
    
    @Override
    public IBlockState getStateFromMeta(int meta) {
        boolean active = (meta & 1) == 1;
        int age = (meta >> 1) & 7;
        return this.getDefaultState()
                   .withProperty(ACTIVATED, meta == 1)
                   .withProperty(AGE, age);
    }
    
    @Override
    public int getMetaFromState(IBlockState state) {
        int i = this.isActivated(state) ? 1 : 0;
        i = i | (this.getAge(state) << 1);
        return i;
    }
    
}
