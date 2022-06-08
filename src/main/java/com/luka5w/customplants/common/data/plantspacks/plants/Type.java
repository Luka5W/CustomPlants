package com.luka5w.customplants.common.data.plantspacks.plants;

import net.minecraft.block.Block;

/**
 * A holder for the parsed #type section of plant config files.
 * @see com.luka5w.customplants.common.data.plantspacks.plants.serialization.Serializer
 */
public class Type {
    
    private final EnumType type;
    
    public Type(EnumType type) {
        this.type = type;
    }
    
    public EnumType getType() {
        return type;
    }
    
    public enum EnumType {
        Bush,
        Crops,
        Extendable,
        Sapling
        
    }
    
    public static class Crops extends Type {
    
        private final boolean canUseBonemeal;
        private final String oreDict;
        private final int reqLightLvl;
    
        public Crops(EnumType type, boolean canUseBonemeal, String oreDict, int reqLightLvl) {
            super(type);
            this.canUseBonemeal = canUseBonemeal;
            this.oreDict = oreDict;
            this.reqLightLvl = reqLightLvl;
        }
    
        public boolean isCanUseBonemeal() {
            return canUseBonemeal;
        }
    
        public String getOreDict() {
            return oreDict;
        }
    
        public int getReqLightvl() {
            return reqLightLvl;
        }
    }
    
    public static class Sapling extends Type {
        
        private final boolean canUseBonemeal;
        private final int reqLightLvl;
        private final Block log;
        private final Block leaf;
    
        public Sapling(EnumType type, boolean canUseBonemeal, int reqLightLvl, Block log, Block leaf) {
            super(type);
            this.canUseBonemeal = canUseBonemeal;
            this.reqLightLvl = reqLightLvl;
            this.log = log;
            this.leaf = leaf;
        }
    
        public boolean isCanUseBonemeal() {
            return canUseBonemeal;
        }
    
        public int getReqLightLvl() {
            return reqLightLvl;
        }
    
        public Block getLogBlock() {
            return log;
        }
    
        public Block getLeafBlock() {
            return leaf;
        }
    }
}
