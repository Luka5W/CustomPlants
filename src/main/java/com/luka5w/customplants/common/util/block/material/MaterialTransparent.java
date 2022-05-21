package com.luka5w.customplants.common.util.block.material;

import net.minecraft.block.material.MapColor;

/**
 * Utility class which is equal to {@link net.minecraft.block.material.MaterialTransparent} but has some more methods with public access.
 */
public class MaterialTransparent extends net.minecraft.block.material.MaterialTransparent {
    private boolean isTranslucent = false;
    
    public MaterialTransparent(MapColor color) {
        super(color);
    }
    
    public MaterialTransparent setTranslucent() {
        this.isTranslucent = true;
        return this;
    }
    
    public MaterialTransparent setRequiresTool() {
        super.setRequiresTool();
        return this;
    }
    
    public MaterialTransparent setBurning() {
        super.setBurning();
        return this;
    }
    
    public boolean isOpaque() {
        return this.isTranslucent ? false : this.blocksMovement();
    }
    
    public MaterialTransparent setNoPushMobility() {
        super.setNoPushMobility();
        return this;
    }
    
    public MaterialTransparent setImmovableMobility() {
        super.setImmovableMobility();
        return this;
    }
    
    public MaterialTransparent setAdventureModeExempt() {
        super.setAdventureModeExempt();
        return this;
    }
}
