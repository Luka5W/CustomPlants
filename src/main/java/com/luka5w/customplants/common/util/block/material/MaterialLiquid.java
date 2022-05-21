package com.luka5w.customplants.common.util.block.material;

import net.minecraft.block.material.MapColor;

/**
 * Utility class which is equal to {@link net.minecraft.block.material.MaterialLiquid} but has some more methods with public access.
 */
public class MaterialLiquid extends net.minecraft.block.material.MaterialLiquid {
    private boolean isTranslucent = false;
    
    public MaterialLiquid(MapColor color) {
        super(color);
    }
    
    public MaterialLiquid setTranslucent() {
        this.isTranslucent = true;
        return this;
    }
    
    public MaterialLiquid setRequiresTool() {
        super.setRequiresTool();
        return this;
    }
    
    public MaterialLiquid setBurning() {
        super.setBurning();
        return this;
    }
    
    public boolean isOpaque() {
        return this.isTranslucent ? false : this.blocksMovement();
    }
    
    public MaterialLiquid setNoPushMobility() {
        super.setNoPushMobility();
        return this;
    }
    
    public MaterialLiquid setImmovableMobility() {
        super.setImmovableMobility();
        return this;
    }
    
    public MaterialLiquid setAdventureModeExempt() {
        super.setAdventureModeExempt();
        return this;
    }
}
