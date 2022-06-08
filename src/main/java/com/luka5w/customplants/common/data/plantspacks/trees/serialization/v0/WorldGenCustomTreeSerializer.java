package com.luka5w.customplants.common.data.plantspacks.trees.serialization.v0;

import com.luka5w.customplants.common.gen.WorldGenCustomTree;
import net.minecraft.block.Block;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

public class WorldGenCustomTreeSerializer {
    
    public static WorldGenCustomTree[] deserialize(Logger logger, boolean notify, Block log, Block leaf, String blocks)
            throws DuplicateRootException, IllegalCharacterException {
        WorldGenCustomTree[] w = new WorldGenCustomTree[EnumFacing.values().length];
        for (EnumFacing f : EnumFacing.values()) {
            w[f.ordinal()] = deserialize(logger, notify, log, leaf, blocks, f);
        }
        return w;
    }
    
    public static WorldGenCustomTree deserialize(Logger logger, boolean notify, Block log, Block leaf, String blocks, EnumFacing facing)
            throws IllegalCharacterException, DuplicateRootException {
        ArrayList<Tuple<BlockPos, Block>> blockList = new ArrayList<>();
        BlockPos root = null;
        String[] layers = blocks.split("\n\n");
        if (facing == EnumFacing.DOWN || facing == EnumFacing.NORTH || facing == EnumFacing.WEST) {
            layers = reverseLayers(layers);
            facing = facing.getOpposite();
        }
        int zLength = layers.length;
        int yLength = 0;
        int xLength = 0;
        int i = 0;
        for (int y = 0; y < layers.length; i++, y++) {
            String[] layer = layers[y].split("\n");
            yLength = Math.max(yLength, layer.length);
            for (int z = 0; z < layer.length; i++, z++) {
                xLength = Math.max(xLength, layer[z].length());
                for (int x = 0; x < layer[z].length(); i++, x++) {
                    char c = layer[z].charAt(x);
                    if (c == ' ' || c == '.') continue;
                    Block block;
                    // position for logging (= the coordinates in the file)
                    BlockPos pos1 = new BlockPos(x, y, z);
                    // position for mc, with rotation depending on facing
                    BlockPos pos = swapCoordinatesForFacing(facing, pos1);
                    if (c == 'O') {
                        if (root != null) throw new DuplicateRootException(pos1, c);
                        root = pos;
                        block = log;
                    }
                    else if (c == 'o') {
                        block = log;
                    }
                    else if (c == 'x') {
                        block = leaf;
                    }
                    else {
                        logger.warn(new IllegalCharacterException(pos1, c));
                        continue;
                    }
                
                    blockList.add(new Tuple<>(pos, block));
                
                }
            }
        }
        if (root == null) {
            root = swapCoordinatesForFacing(facing, new BlockPos(xLength / 2, 0, zLength / 2));
            logger.warn(new MissingRootException(root, 'O', i));
        }
        return new WorldGenCustomTree(notify, blockList, root);
    }
    
    public static String[] reverseLayers(String[] layers) {
        String[] layersReversed = new String[layers.length];
        for (int i = 0, j = layers.length - 1; i < layers.length; i++, j--) {
            layersReversed[i] = layers[j];
        }
        return layersReversed;
    }
    
    private static BlockPos swapCoordinatesForFacing(EnumFacing f, BlockPos pos) {
        if (f == EnumFacing.UP) return pos;
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        if (f == EnumFacing.SOUTH) return new BlockPos(x, z, y);
        if (f == EnumFacing.EAST) return new BlockPos(y, x, z);
        throw new IllegalStateException("Can't rotate by 180°");
    }
    
    public static class IllegalCharacterException extends java.text.ParseException {
        
        public IllegalCharacterException(BlockPos pos, char c) {
            super("Illegal character at " + pos + ": " + c, pos.getX() * pos.getY() * pos.getZ());
        }
    }
    
    public static class DuplicateRootException extends java.text.ParseException {
        
        public DuplicateRootException(BlockPos pos, char c) {
            super("Duplicate root at " + pos + ": " + c, pos.getX() * pos.getY() * pos.getZ());
        }
    }
    
    public static class MissingRootException extends java.text.ParseException {
        
        public MissingRootException(BlockPos pos, char c, int i) {
            super("Missing root, set root to " + pos + ": " + c, i);
        }
    }
}
