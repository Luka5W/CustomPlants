package com.luka5w.customplants.common.blocks;

import com.luka5w.customplants.common.plantsfeatures.CustomPlantActions;
import com.luka5w.customplants.common.plantsfeatures.CustomPlantFeatures;
import com.luka5w.customplants.common.tileentities.TileEntityCustomBush;
import com.luka5w.customplants.common.util.TargetSelectors;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class BlockCustomBush extends Block implements IPlantable {
    
    public static final PropertyBool ACTIVATED = PropertyBool.create("activated");
    
    private final AxisAlignedBB axisAlignedBB;
    private final EnumFacing facing;
    private final EnumPlantType type;
    private final boolean soilsAllowed;
    private final List<String> soilsList;
    private final CustomPlantActions actions;
    private final CustomPlantFeatures features;
    private BlockFaceShape blockFaceShape;
    
    public BlockCustomBush(AxisAlignedBB axisAlignedBB,
                           BlockFaceShape blockFaceShape,
                           EnumFacing facing,
                           Material blockMaterial,
                           EnumPlantType type,
                           CustomPlantActions actions,
                           CustomPlantFeatures features) {
        this(axisAlignedBB, blockFaceShape, facing, blockMaterial, type, actions, features, false, null);
    }
    
    public BlockCustomBush(AxisAlignedBB axisAlignedBB,
                           BlockFaceShape blockFaceShape,
                           EnumFacing facing,
                           Material blockMaterial,
                           EnumPlantType type,
                           CustomPlantActions actions,
                           CustomPlantFeatures features,
                           boolean soilsAllowed,
                           @Nullable List<String> soilsList) {
        super(blockMaterial);
        this.setTickRandomly(true);
        this.axisAlignedBB = axisAlignedBB;
        this.blockFaceShape = blockFaceShape;
        this.facing = facing;
        this.type = type;
        this.actions = actions;
        this.features = features;
        this.soilsAllowed = soilsAllowed;
        this.soilsList = soilsList;
    }
    
    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(ACTIVATED, meta == 1);
    }
    
    @Override
    public int getMetaFromState(IBlockState state) {
        return this.isActivated(state) ? 1 : 0;
    }
    
    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        // TODO: 06.05.22 enhance
        return super.getItemDropped(state, rand, fortune);
    }
    
    @Override
    public int quantityDropped(IBlockState state, int fortune, Random random) {
        // TODO: 06.05.22 enhance
        return super.quantityDropped(state, fortune, random);
    }
    
    @Override
    public EnumPlantType getPlantType(IBlockAccess world, BlockPos pos) {
        return this.type;
    }
    
    @Override
    public IBlockState getPlant(IBlockAccess world, BlockPos pos) {
        IBlockState state = world.getBlockState(pos);
        if (state.getBlock() != this) return getDefaultState();
        return state;
    }
    
    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        return this.canBlockStay(worldIn, pos) && (this.soilsList != null || super.canPlaceBlockAt(worldIn, pos));
    }
    
    @Override
    public boolean hasTileEntity(IBlockState state) {
        return this.features.hasEffects();
    }
    
    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return this.hasTileEntity(state) ? new TileEntityCustomBush() : null;
    }
    
    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        this.checkAndDropBlock(worldIn, pos, state);
    }
    
    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        this.checkAndDropBlock(worldIn, pos, state);
    }
    
    protected void checkAndDropBlock(World worldIn, BlockPos pos, IBlockState state) {
        if (!this.canBlockStay(worldIn, pos))
        {
            this.dropBlockAsItem(worldIn, pos, state, 0);
            worldIn.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
        }
    }
    
    public boolean canBlockStay(World worldIn, BlockPos pos) {
        BlockPos soilPos = pos.offset(this.facing.getOpposite());
        IBlockState soil = worldIn.getBlockState(soilPos);
        if (this.soilsList == null) {
            return soil.getBlock().canSustainPlant(soil, worldIn, soilPos, this.facing, this);
        }
        else {
            ResourceLocation soilsRes = soil.getBlock().getRegistryName();
            if (soilsRes == null) return false; // don't allow this plant to be planted on blocks without a name
            // allow this plant to be planted on allowed blocks or on every block which is not denied
            return soilsAllowed == (this.soilsList.contains(soilsRes.toString()) ||
                                    this.soilsList.contains(soilsRes + ":" + soil.getBlock().getMetaFromState(soil)));
        }
    }
    
    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return this.axisAlignedBB;
    }
    
    @Nullable
    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
        return this.isSolid(blockState) ? this.axisAlignedBB : Block.NULL_AABB;
    }
    
    @Override
    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }
    
    @Override
    public boolean isFullCube(IBlockState state)
    {
        return false;
    }
    
    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, ACTIVATED);
    }
    
    // feature handling
    
    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
                                    EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (worldIn.isRemote) return true;
        boolean activated = isActivated(state);
        if (activated) {
            this.actions.onDeactivated(worldIn, pos, state, this.isMature(state), playerIn, hand, facing, hitX, hitY, hitZ);
        }
        else {
            this.actions.onActivated(worldIn, pos, state, this.isMature(state), playerIn, hand, facing, hitX, hitY, hitZ);
        }
        worldIn.setBlockState(pos, getDefaultState().withProperty(ACTIVATED, !activated));
        return true;
    }
    
    @Override
    public void onBlockClicked(World worldIn, BlockPos pos, EntityPlayer playerIn) {
        IBlockState state = worldIn.getBlockState(pos);
        if (!worldIn.isRemote) this.actions.onDestroying(worldIn, pos, state, this.isActivated(state), this.isMature(state), playerIn);
    }
    
    @Override
    public void onBlockDestroyedByPlayer(World worldIn, BlockPos pos, IBlockState state) {
        if (!worldIn.isRemote) this.actions.onDestroyed(worldIn, pos, state, this.isActivated(state), this.isMature(state));
    }
    
    @Override
    public void onBlockDestroyedByExplosion(World worldIn, BlockPos pos, Explosion explosionIn) {
        IBlockState state = worldIn.getBlockState(pos);
        if (!worldIn.isRemote) this.actions.onDestroyed(worldIn, pos, state, this.isActivated(state), this.isMature(state));
    }
    
    @Override
    public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
        if (!worldIn.isRemote) {
            if (this.features.getBoolFeature(this.isActivated(state), this.isMature(state),
                                             CustomPlantFeatures.EnumPlantFeature.IsWeb, true))
                entityIn.setInWeb();
            this.actions.onEntityCollided(worldIn, pos, state, this.isActivated(state), this.isMature(state), entityIn);
        }
    }
    
    // TODO: 05.05.22
    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return this.isSolid(state) ? this.blockFaceShape : BlockFaceShape.UNDEFINED;
    }
    
    // TODO: 05.05.22
    @Override
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT;
    }
    
    @Override
    public boolean isTopSolid(IBlockState state) {
        return this.isSolid(state);
    }
    
    @Override
    public boolean isSideSolid(IBlockState base_state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        return this.isSolid(base_state);
    }
    
    @Override
    public boolean isLadder(IBlockState state, IBlockAccess world, BlockPos pos, EntityLivingBase entity) {
        return this.features.getBoolFeature(this.isActivated(state), this.isMature(state),
                                            CustomPlantFeatures.EnumPlantFeature.IsLadder, true);
    }
    
    @Override
    public boolean canSustainLeaves(IBlockState state, IBlockAccess world, BlockPos pos) {
        return this.features.getBoolFeature(this.isActivated(state), this.isMature(state),
                                            CustomPlantFeatures.EnumPlantFeature.CanSustainLeaves, true);
    }
    
    @Override
    public float getEnchantPowerBonus(World world, BlockPos pos) {
        IBlockState state = world.getBlockState(pos);
        return this.features.getFloatFeature(this.isActivated(state),
                                             this.isMature(state),
                                             CustomPlantFeatures.EnumPlantFeature.GetEnchantPowerBonus,
                                             CustomPlantFeatures.EnumNumberFilter.Sum);
    }
    
    public void addEffects(World worldIn, BlockPos pos, IBlockState state) {
        List<Tuple<PotionEffect, String>> effects = this.features.getEffects(this.isActivated(state), this.isMature(state));
        if (effects != null && !effects.isEmpty()) {
            for (Tuple<PotionEffect, String> effect : effects) {
                List<Entity> targets;
                try {
                    targets = TargetSelectors.getMatchingEntities(worldIn, pos, state, effect.getSecond());
                }
                catch (CommandException e) {
                    // TODO: 08.05.22 misconfiguration
                    break;
                }
                if (!targets.isEmpty())
                    for (Entity target : targets)
                        if (target instanceof EntityLivingBase) {
                            PotionEffect oldEffect = effect.getFirst();
                            PotionEffect newEffect = new PotionEffect(effect.getFirst());
                            ((EntityLivingBase) target).addPotionEffect(new PotionEffect(effect.getFirst()));
                        }
            }
        }
    }
    
    public boolean isActivated(IBlockState state) {
        return state.getValue(ACTIVATED);
    }
    
    public boolean isMature(IBlockState state) {
        return true;
    }
    
    public boolean isSolid(IBlockState state) {
        return this.features.getBoolFeature(this.isActivated(state), this.isMature(state),
                                            CustomPlantFeatures.EnumPlantFeature.IsSolid, true);
    }
}
