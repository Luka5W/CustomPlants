package com.luka5w.customplants.common.blocks;

import com.luka5w.customplants.common.items.ItemCustomSeeds;
import com.luka5w.customplants.common.plantsbehavior.PlantBehavior;
import net.minecraft.block.Block;
import net.minecraft.block.IGrowable;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.ForgeHooks;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class BlockCustomCrops extends BlockCustomPlant implements IGrowable {
    
    public static final PropertyBool ACTIVATED = PropertyBool.create("activated");
    private static final int MAX_AGE = 7;
    public static final PropertyInteger AGE = PropertyInteger.create("age", 0, MAX_AGE);
    private final boolean canUseBonemeal;
    private final ItemCustomSeeds seeds;
    
    public BlockCustomCrops(AxisAlignedBB[] aabbs, PlantBehavior behavior, Material blockMaterial, BlockFaceShape blockShape,
                            @Nullable List<Tuple<Float, ItemStack>> drops, EnumFacing facing, String oreDict,
                            EnumPlantType type, boolean canUseBonemeal, ItemCustomSeeds seeds,
                            @Nullable ArrayList<String> soilsList, boolean soilsAllowed) {
        super(blockMaterial, aabbs, blockShape, behavior, drops, Collections.singletonList(facing),
              soilsList, soilsAllowed, type);
        this.canUseBonemeal = canUseBonemeal;
        this.seeds = seeds;
    }
    
    // PLACEMENT
    
    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos,
                                  EntityPlayer player) {
        return new ItemStack(this.seeds);
    }
    
    // TICKS/ UPDATES, TE
    
    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        this.checkAndDropBlock(worldIn, pos, state);
    
        // TODO: 06.06.22 how does this method work?
        //if (!worldIn.isAreaLoaded(pos, 1)) return; // Forge: prevent loading unloaded chunks when checking neighbor's light
        //if (worldIn.getLightFromNeighbors(pos.up()) >= 9) {
        int i = this.getAge(state);
    
        if (i < this.getMaxAge()) {
            float f = getGrowthChance(this, worldIn, pos);
        
            if (ForgeHooks.onCropsGrowPre(worldIn, pos, state, rand.nextInt((int)(25.0F / f) + 1) == 0)) {
                worldIn.setBlockState(pos, this.setAge(i + 1, state), 2);
                ForgeHooks.onCropsGrowPost(worldIn, pos, state, worldIn.getBlockState(pos));
            }
        }
        //}
    }
    
    protected static float getGrowthChance(Block blockIn, World worldIn, BlockPos pos) {
        float f = 1.0F;
        BlockPos blockpos = pos.down();
        
        for (int i = -1; i <= 1; ++i)
        {
            for (int j = -1; j <= 1; ++j)
            {
                float f1 = 0.0F;
                IBlockState iblockstate = worldIn.getBlockState(blockpos.add(i, 0, j));
                
                if (iblockstate.getBlock().canSustainPlant(iblockstate, worldIn, blockpos.add(i, 0, j), net.minecraft.util.EnumFacing.UP, (net.minecraftforge.common.IPlantable)blockIn))
                {
                    f1 = 1.0F;
                    
                    if (iblockstate.getBlock().isFertile(worldIn, blockpos.add(i, 0, j)))
                    {
                        f1 = 3.0F;
                    }
                }
                
                if (i != 0 || j != 0)
                {
                    f1 /= 4.0F;
                }
                
                f += f1;
            }
        }
        
        BlockPos blockpos1 = pos.north();
        BlockPos blockpos2 = pos.south();
        BlockPos blockpos3 = pos.west();
        BlockPos blockpos4 = pos.east();
        boolean flag = blockIn == worldIn.getBlockState(blockpos3).getBlock() || blockIn == worldIn.getBlockState(blockpos4).getBlock();
        boolean flag1 = blockIn == worldIn.getBlockState(blockpos1).getBlock() || blockIn == worldIn.getBlockState(blockpos2).getBlock();
        
        if (flag && flag1)
        {
            f /= 2.0F;
        }
        else
        {
            boolean flag2 = blockIn == worldIn.getBlockState(blockpos3.north()).getBlock() || blockIn == worldIn.getBlockState(blockpos4.north()).getBlock() || blockIn == worldIn.getBlockState(blockpos4.south()).getBlock() || blockIn == worldIn.getBlockState(blockpos3.south()).getBlock();
            
            if (flag2)
            {
                f /= 2.0F;
            }
        }
        
        return f;
    }
    
    // GROWTH
    
    @Override
    public boolean canGrow(World worldIn, BlockPos pos, IBlockState state, boolean isClient) {
        return this.getAge(state) < this.aabbs.length;
    }
    
    @Override
    public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, IBlockState state) {
        return this.canUseBonemeal;
    }
    
    @Override
    public void grow(World worldIn, Random rand, BlockPos pos, IBlockState state) {
        int i = this.getAge(state) + MathHelper.getInt(worldIn.rand, 2, 5);
        int j = this.getMaxAge();
        if (i > j) i = j;
        worldIn.setBlockState(pos, this.setAge(i, state), 2);
    }
    
    // PROPERTIES
    
    @Override
    protected AxisAlignedBB getAABB(IBlockState state) {
        return this.aabbs[this.getAge(state)];
    }
    
    @Override
    protected IBlockState setActivated(boolean activatedOut, IBlockState stateIn) {
        return getDefaultState().withProperty(ACTIVATED, activatedOut)
                                .withProperty(AGE, this.getAge(stateIn));
    }
    
    @Override
    protected boolean isActivated(IBlockState state) {
        return state.getValue(ACTIVATED);
    }
    
    protected IBlockState setAge(int age, IBlockState stateIn) {
        return getDefaultState().withProperty(ACTIVATED, this.isActivated(stateIn))
                                .withProperty(AGE, age);
    }
    
    protected int getAge(IBlockState state) {
        return state.getValue(AGE);
    }
    
    protected int getMaxAge() {
        return Math.min(MAX_AGE, this.aabbs.length);
    }
    
    // STATES, PLANT TYPE
    
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
