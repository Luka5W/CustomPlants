package com.luka5w.customplants.common.tileentities;

import com.luka5w.customplants.common.blocks.BlockCustomPlant;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TileEntityCustomPlant extends TileEntity implements ITickable {
    
    public TileEntityCustomPlant() {
    
    }
    
    @Override
    public void update() {
        if (this.world != null && !this.world.isRemote && this.world.getTotalWorldTime() % 80L == 0L) {
            IBlockState state = this.world.getBlockState(this.pos);
            ((BlockCustomPlant) state.getBlock()).addEffects(this.world, this.pos, state);
        }
    }
    
    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
        return oldState.getBlock() != newSate.getBlock();
    }
    
    @Override
    protected void setWorldCreate(World worldIn) {
        this.setWorld(worldIn);
    }
}
