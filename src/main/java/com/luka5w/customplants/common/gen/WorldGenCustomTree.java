package com.luka5w.customplants.common.gen;

import net.minecraft.block.Block;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;

import java.util.ArrayList;
import java.util.Random;

public class WorldGenCustomTree extends WorldGenAbstractTree {
    
    private final ArrayList<Tuple<BlockPos, Block>> blocks;
    private final BlockPos root;
    
    public WorldGenCustomTree(boolean notify, ArrayList<Tuple<BlockPos, Block>> blocks, BlockPos root) {
        super(notify);
        this.blocks = blocks;
        this.root = root;
    }
    
    /**
     * Generates a tree from the passed blocks.
     * @param worldIn
     * @param rand
     * @param position
     * @return Whether the tree was generated successfully.
     */
    @Override
    public boolean generate(World worldIn, Random rand, BlockPos position) {
        BlockPos[] targets = new BlockPos[this.blocks.size()];
        ArrayList<Tuple<BlockPos, Block>> tuples = this.blocks;
        BlockPos coordinateRoot = position.subtract(this.root);
        for (int i = 0; i < tuples.size(); i++) {
            Tuple<BlockPos, Block> pos = tuples.get(i);
            targets[i] = coordinateRoot.add(pos.getFirst());
            if (!this.isReplaceable(worldIn, targets[i])) return false;
        }
        ArrayList<Tuple<BlockPos, Block>> tupleArrayList = this.blocks;
        for (int i = 0; i < tupleArrayList.size(); i++) {
            Block block = tupleArrayList.get(i).getSecond();
            this.setBlockAndNotifyAdequately(worldIn, targets[i], block.getDefaultState());
        }
        return true;
    }
}
