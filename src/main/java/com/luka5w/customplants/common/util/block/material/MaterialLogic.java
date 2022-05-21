package com.luka5w.customplants.common.util.block.material;

import net.minecraft.block.material.MapColor;

/**
 * Utility class which is equal to {@link net.minecraft.block.material.MaterialLogic} but has some more methods with public access.
 */
public class MaterialLogic extends net.minecraft.block.material.MaterialLogic {
    private boolean isTranslucent = false;
    
    public MaterialLogic(MapColor color) {
        super(color);
    }
    
    public MaterialLogic setTranslucent() {
        this.isTranslucent = true;
        return this;
    }
    
    public MaterialLogic setRequiresTool() {
        super.setRequiresTool();
        return this;
    }
    
    public MaterialLogic setBurning() {
        super.setBurning();
        return this;
    }
    
    public boolean isOpaque() {
        return this.isTranslucent ? false : this.blocksMovement();
    }
    
    public MaterialLogic setNoPushMobility() {
        super.setNoPushMobility();
        return this;
    }
    
    public MaterialLogic setImmovableMobility() {
        super.setImmovableMobility();
        return this;
    }
    
    public MaterialLogic setAdventureModeExempt() {
        super.setAdventureModeExempt();
        return this;
    }
    
}
