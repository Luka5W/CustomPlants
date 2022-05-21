package com.luka5w.customplants.common.util.block.material;

import net.minecraft.block.material.MapColor;

/**
 * Utility class which is equal to {@link net.minecraft.block.material.MaterialPortal} but has some more methods with public access.
 */
public class MaterialPortal extends net.minecraft.block.material.MaterialPortal {
    private boolean isTranslucent = false;
    
    public MaterialPortal(MapColor color) {
        super(color);
    }
    
    public MaterialPortal setTranslucent() {
        this.isTranslucent = true;
        return this;
    }
    
    public MaterialPortal setRequiresTool() {
        super.setRequiresTool();
        return this;
    }
    
    public MaterialPortal setBurning() {
        super.setBurning();
        return this;
    }
    
    public boolean isOpaque() {
        return this.isTranslucent ? false : this.blocksMovement();
    }
    
    public MaterialPortal setNoPushMobility() {
        super.setNoPushMobility();
        return this;
    }
    
    public MaterialPortal setImmovableMobility() {
        super.setImmovableMobility();
        return this;
    }
    
    public MaterialPortal setAdventureModeExempt() {
        super.setAdventureModeExempt();
        return this;
    }
}
