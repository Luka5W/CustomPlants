package com.luka5w.customplants.common.data.plantspacks.plants;

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
        Overlay,
        Sapling
        
    }
    
    public static class Crops extends Type {
    
        private final boolean canUseBonemeal;
    
        public Crops(EnumType type, boolean canUseBonemeal) {
            super(type);
            this.canUseBonemeal = canUseBonemeal;
        }
    
        public boolean isCanUseBonemeal() {
            return canUseBonemeal;
        }
    }
}
