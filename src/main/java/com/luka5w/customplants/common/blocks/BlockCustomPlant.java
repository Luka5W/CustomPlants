package com.luka5w.customplants.common.blocks;

import com.luka5w.customplants.common.plantsbehavior.PlantBehavior;
import com.luka5w.customplants.common.tileentities.TileEntityCustomPlant;
import com.luka5w.customplants.common.util.TargetSelectors;
import com.luka5w.customplants.common.util.debug_please_remove.DebugUtils;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
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

public class BlockCustomPlant extends Block implements IPlantable {
    
    public static final PropertyBool ACTIVATED = PropertyBool.create("activated");
    protected static TileEntityCustomPlant tileEntity = null;
    
    protected final AxisAlignedBB[] aabbs;
    protected final BlockFaceShape blockShape;
    protected final List<Tuple<Float, ItemStack>> drops;
    protected final List<EnumFacing> facings;
    protected final PlantBehavior behavior;
    @Nullable
    protected final List<String> soilsList;
    protected final boolean soilsAllowed;
    protected final EnumPlantType type;
    
    public BlockCustomPlant(Material materialIn, AxisAlignedBB[] aabbs, BlockFaceShape blockShape, PlantBehavior behavior,
                            @Nullable List<Tuple<Float, ItemStack>> drops, List<EnumFacing> facings,
                            @Nullable List<String> soilsList, boolean soilsAllowed, EnumPlantType type) {
        super(materialIn);
        this.aabbs = aabbs;
        this.blockShape = blockShape;
        this.behavior = behavior;
        this.drops = drops;
        this.facings = facings;
        this.soilsList = soilsList;
        this.soilsAllowed = soilsAllowed;
        this.type = type;
        this.setDefaultState(this.createDefaultBlockState(this.blockState.getBaseState()));
    }
    
    // PLACEMENT
    
    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        for (EnumFacing enumfacing : EnumFacing.values()) {
            if (this.canPlaceBlockOnSide(worldIn, pos, enumfacing)) return true;
        }
        return false;
    }
    
    @Override
    public boolean canPlaceBlockOnSide(World worldIn, BlockPos pos, EnumFacing side) {
        return this.facings.contains(side) &&
               this.canBlockStay(worldIn, pos, this.getDefaultState()) &&
               (this.soilsList != null || super.canPlaceBlockAt(worldIn, pos));
    }
    
    protected void checkAndDropBlock(World worldIn, BlockPos pos, IBlockState state) {
        if (!this.canBlockStay(worldIn, pos, state))
        {
            this.dropBlockAsItem(worldIn, pos, state, 0);
            worldIn.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
        }
    }
    
    protected boolean canBlockStay(World worldIn, BlockPos pos, IBlockState state) {
        BlockPos soilPos = pos.offset(this.getFacing(state).getOpposite());
        IBlockState soil = worldIn.getBlockState(soilPos);
        if (this.soilsList == null) {
            return soil.getBlock().canSustainPlant(soil, worldIn, soilPos, this.getFacing(state), this);
        }
        else {
            ResourceLocation soilsRes = soil.getBlock().getRegistryName();
            if (soilsRes == null) return false; // don't allow this plant to be planted on blocks without a name
            // allow this plant to be planted on allowed blocks or on every block which is not denied
            return this.soilsAllowed == (this.soilsList.contains(soilsRes.toString()) ||
                                    this.soilsList.contains(soilsRes + ":" + soil.getBlock().getMetaFromState(soil)));
        }
    }
    
    // APPEARANCE/ SHAPE
    
    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        DebugUtils.releaseMouse(null, true);
        return this.aabbs[this.getAge(state)];
    }
    
    @Nullable
    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
        return this.isSolid(blockState) ? this.aabbs[this.getAge(blockState)] : Block.NULL_AABB;
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
    }// TODO: 05.05.22
    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return this.isSolid(state) ? this.blockShape : BlockFaceShape.UNDEFINED;
    }
    
    // TODO: 05.05.22
    @Override
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT;
    }
    
    // APPEARANCE/ FEATURES
    
    protected boolean isSolid(IBlockState state) {
        return this.behavior.getBoolFeature(this.isActivated(state), this.getAge(state),
                                            PlantBehavior.EnumPlantFeature.IsSolid);
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
        return this.behavior.getBoolFeature(this.isActivated(state), this.getAge(state),
                                            PlantBehavior.EnumPlantFeature.IsLadder);
    }
    
    @Override
    public boolean canSustainLeaves(IBlockState state, IBlockAccess world, BlockPos pos) {
        return this.behavior.getBoolFeature(this.isActivated(state), this.getAge(state),
                                            PlantBehavior.EnumPlantFeature.CanSustainLeaves);
    }
    
    @Override
    public float getEnchantPowerBonus(World world, BlockPos pos) {
        IBlockState state = world.getBlockState(pos);
        return this.behavior.getFloatFeature(this.isActivated(state), this.getAge(state),
                                             PlantBehavior.EnumPlantFeature.GetEnchantPowerBonus);
    }
    
    // FEATURES
    
    @Override
    public void getDrops(net.minecraft.util.NonNullList<ItemStack> drops, net.minecraft.world.IBlockAccess world,
                                  BlockPos pos, IBlockState state, int fortune) {
        if (this.drops != null && this.drops.isEmpty()) {
            for (Tuple<Float, ItemStack> drop : this.drops) {
                Random rand = world instanceof World ? ((World) world).rand : RANDOM;
                ItemStack stack = drop.getSecond();
                for (int i = 0; i < stack.getCount(); i++) {
                    if (stack.getItem() != Items.AIR && this.doDrop(rand, fortune, drop.getFirst()))
                        drops.add(new ItemStack(
                                stack.getItem(),
                                1,
                                stack.getItemDamage()));
                }
            }
        }
    }
    
    // TODO: 06.06.22 find a better implementation for random drops
    protected boolean doDrop(Random rand, int fortune, float rounds) {
        if (rounds == 0) return true;
        int success = (fortune + 2) / 2;
        for (int i = 0; i < rounds; i++) {
            if (rand.nextInt(fortune + 2) / 2 > success) return true;
        }
        return false;
    }
    
    @Override
    protected ItemStack getSilkTouchDrop(IBlockState state) {
        return super.getSilkTouchDrop(state);
    }
    
    
    
    @Override
    protected NonNullList<ItemStack> captureDrops(boolean start) {
        return super.captureDrops(start);
    }
    
    // TICKS/ UPDATES, TE
    
    @Override
    public boolean hasTileEntity(IBlockState state) {
        boolean b = this.behavior.hasFeatureEffects();
        return b;
    }
    
    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        if (!this.hasTileEntity(state)) return null;
        if (BlockCustomPlant.tileEntity == null) BlockCustomPlant.tileEntity = new TileEntityCustomPlant();
        return BlockCustomPlant.tileEntity;
    }
    
    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        this.checkAndDropBlock(worldIn, pos, state);
    }
    
    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        this.checkAndDropBlock(worldIn, pos, state);
    }
    
    // TICKS/ EVENTS
    
    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
                                    EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (worldIn.isRemote) return true;
        boolean activatedOut = !this.isActivated(state);
        this.behavior.onActivated(activatedOut, this.getAge(state), worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);
        worldIn.setBlockState(pos, this.setActivated(activatedOut, state));
        return true;
    }
    
    @Override
    public void onBlockClicked(World worldIn, BlockPos pos, EntityPlayer playerIn) {
        IBlockState state = worldIn.getBlockState(pos);
        if (!worldIn.isRemote) this.behavior.onDestroying(this.isActivated(state), this.getAge(state), worldIn, pos, state, playerIn);
    }
    
    @Override
    public void onBlockDestroyedByPlayer(World worldIn, BlockPos pos, IBlockState state) {
        if (!worldIn.isRemote) this.behavior.onDestroyed(this.isActivated(state), this.getAge(state), worldIn, pos, state);
    }
    
    @Override
    public void onBlockDestroyedByExplosion(World worldIn, BlockPos pos, Explosion explosionIn) {
        IBlockState state = worldIn.getBlockState(pos);
        if (!worldIn.isRemote) this.behavior.onDestroyed(this.isActivated(state), this.getAge(state), worldIn, pos, state);
    }
    
    @Override
    public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
        if (!worldIn.isRemote) {
            if (entityIn instanceof EntityLiving) {
                // NPC
                this.behavior.onCollidedNPC(this.isActivated(state), this.getAge(state), worldIn, pos, state, (EntityLiving) entityIn);
            }
            else if (entityIn instanceof EntityPlayer) {
                // Player
                this.behavior.onCollidedPlayer(this.isActivated(state), this.getAge(state), worldIn, pos, state, (EntityPlayer) entityIn);
            }
            else {
                // Item
                this.behavior.onCollidedEntity(this.isActivated(state), this.getAge(state), worldIn, pos, state, entityIn);
            }
        }
    }
    
    // PROPERTIES
    
    protected IBlockState setActivated(boolean activatedOut, IBlockState stateIn) {
        return getDefaultState().withProperty(ACTIVATED, activatedOut);
    }
    
    protected boolean isActivated(IBlockState state) {
        return state.getValue(ACTIVATED);
    }
    
    protected EnumFacing getFacing(IBlockState state) {
        return this.facings.get(this.getAge(state));
    }
    
    protected int getAge(IBlockState state) {
        return 0;
    }
    
    public void addEffects(World worldIn, BlockPos pos, IBlockState state) {
        if (!this.behavior.hasFeatureEffects() ||
            !this.behavior.hasFeatureEffects(this.isActivated(state), this.getAge(state)))
            return;
        boolean activated = this.isActivated(state);
        int age = this.getAge(state);
        List<Tuple<String, PotionEffect>> effects = this.behavior.getFeatureEffects(activated, age);
        if (effects != null && !effects.isEmpty()) {
            for (int i = 0; i < effects.size(); i++) {
                Tuple<String, PotionEffect> effect = effects.get(i);
                List<Entity> targets;
                try {
                    targets = TargetSelectors.getMatchingEntities(worldIn, pos, state, effect.getFirst());
                }
                catch (CommandException e) {
                    this.behavior.removeFeatureEffect(activated, age, i);
                    // TODO: 08.05.22 misconfiguration
                    break;
                }
                if (!targets.isEmpty())
                    for (Entity target : targets)
                        if (target instanceof EntityLivingBase) {
                            ((EntityLivingBase) target).addPotionEffect(new PotionEffect(effect.getSecond()));
                        }
            }
        }
    }
    
    // STATES, PLANT TYPE
    
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
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, ACTIVATED);
    }
    
    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState()
                   .withProperty(ACTIVATED, meta == 1);
    }
    
    @Override
    public int getMetaFromState(IBlockState state) {
        int i = this.isActivated(state) ? 1 : 0;
        return i;
    }
    
    protected IBlockState createDefaultBlockState(IBlockState stateIn) {
        return stateIn.withProperty(ACTIVATED, false);
    }
}
