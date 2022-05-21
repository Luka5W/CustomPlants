package com.luka5w.customplants.common.util.block.material;

import net.minecraft.block.material.MapColor;

/**
 * Utility class which is equal to {@link net.minecraft.block.material.Material} but has some more methods with public access.
 */
public class Material extends net.minecraft.block.material.Material {
    private boolean isTranslucent = false;
    
    public Material(MapColor color) {
        super(color);
    }
    
    public Material setTranslucent() {
        this.isTranslucent = true;
        return this;
    }
    
    public Material setRequiresTool() {
        super.setRequiresTool();
        return this;
    }
    
    public Material setBurning() {
        super.setBurning();
        return this;
    }
    
    public boolean isOpaque() {
        return this.isTranslucent ? false : this.blocksMovement();
    }
    
    public Material setNoPushMobility() {
        super.setNoPushMobility();
        return this;
    }
    
    public Material setImmovableMobility() {
        super.setImmovableMobility();
        return this;
    }
    
    public Material setAdventureModeExempt() {
        super.setAdventureModeExempt();
        return this;
    }
}
