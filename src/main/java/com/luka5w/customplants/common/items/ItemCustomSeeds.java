package com.luka5w.customplants.common.items;

import com.luka5w.customplants.common.blocks.BlockCustomCrops;
import com.luka5w.customplants.common.util.debug_please_remove.DebugUtils;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;

import java.util.List;

public class ItemCustomSeeds extends Item implements net.minecraftforge.common.IPlantable {
    
    private final EnumPlantType type;
    private final BlockCustomCrops crops;
    private final List<EnumFacing> facings;
    
    public ItemCustomSeeds(EnumPlantType type, BlockCustomCrops crops, List<EnumFacing> facings) {
        this.type = type;
        this.crops = crops;
        this.facings = facings;
    }
    
    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing,
                                      float hitX, float hitY, float hitZ) {
        DebugUtils.releaseMouse(worldIn, true);
        ItemStack itemstack = player.getHeldItem(hand);
        net.minecraft.block.state.IBlockState state = worldIn.getBlockState(pos);
        if (this.facings.contains(facing) &&
            player.canPlayerEdit(pos.offset(facing), facing, itemstack) &&
            this.crops.canPlaceBlockAt(worldIn, pos.offset(facing)) &&
            worldIn.isAirBlock(pos.offset(facing))) {
            worldIn.setBlockState(pos.up(), this.crops.getDefaultState());
        
            if (player instanceof EntityPlayerMP)
            {
                CriteriaTriggers.PLACED_BLOCK.trigger((EntityPlayerMP)player, pos.up(), itemstack);
            }
        
            itemstack.shrink(1);
            return EnumActionResult.SUCCESS;
        }
        else
        {
            return EnumActionResult.FAIL;
        }
    }
    
    @Override
    public EnumPlantType getPlantType(IBlockAccess world, BlockPos pos) {
        return this.type;
    }
    
    @Override
    public IBlockState getPlant(IBlockAccess world, BlockPos pos) {
        return this.crops.getDefaultState();
    }
}
