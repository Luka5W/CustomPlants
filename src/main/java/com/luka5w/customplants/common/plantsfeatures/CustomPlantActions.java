package com.luka5w.customplants.common.plantsfeatures;

import com.luka5w.customplants.common.util.TargetSelectors;
import com.luka5w.customplants.common.util.debug_please_remove.DebugUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class CustomPlantActions {
    
    private final PlantActions collisionActions;
    private final PlantActions destructionActions;
    private final PlantActions destructingActions;
    private final PlantActions activationActions;
    private final PlantActions deactivationActions;
    private final PlantActions growthActions;
    private final PlantActions growthMatureActions;
    
    public CustomPlantActions(PlantActions collisionActions, PlantActions destructionActions,
                              PlantActions destructingActions, PlantActions activationActions,
                              PlantActions deactivationActions, PlantActions growthActions,
                              PlantActions growthMatureActions) {
        this.collisionActions = collisionActions;
        this.destructionActions = destructionActions;
        this.destructingActions = destructingActions;
        this.activationActions = activationActions;
        this.deactivationActions = deactivationActions;
        this.growthActions = growthActions;
        this.growthMatureActions = growthMatureActions;
    }
    
    public void onEntityCollided(World worldIn, BlockPos pos, IBlockState state, boolean isActivated, boolean isMature, Entity entityIn) {
        // PlantEvent:Collided (0)
        this.executeAction(this.collisionActions, worldIn, pos, state, isActivated, isMature);
    }
    
    public void onDestroyed(World worldIn, BlockPos pos, IBlockState state, boolean isActivated, boolean isMature) {
        // PlantEvent:Destroyed (1)
        this.executeAction(this.destructionActions, worldIn, pos, state, isActivated, isMature);
        
    }
    
    public void onDestroying(World worldIn, BlockPos pos, IBlockState state, boolean isActivated, boolean isMature, EntityPlayer playerIn) {
        // PlantEvent:Destroying (2)
        this.executeAction(this.destructingActions, worldIn, pos, state, isActivated, isMature);
        
    }
    
    public boolean onActivated(World worldIn, BlockPos pos, IBlockState state, boolean isMature, EntityPlayer playerIn,
                               EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        // PlantEvent:Activated (3)
        return this.executeAction(this.activationActions, worldIn, pos, state, true, isMature);
    }
    
    public boolean onDeactivated(World worldIn, BlockPos pos, IBlockState state, boolean isMature, EntityPlayer playerIn,
                                 EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        // PlantEvent:Deactivated (4)
        return this.executeAction(this.deactivationActions, worldIn, pos, state, false, isMature);
    }
    
    public void onGrown(World worldIn, BlockPos pos, IBlockState state, boolean isActivated, boolean isMature) {
        if (isMature) {
            // PlantEvent:GrownMature (6)
            this.executeAction(this.growthActions, worldIn, pos, state, isActivated, isMature);
        }
        else {
            // PlantEvent:Grown (5)
            this.executeAction(this.growthMatureActions, worldIn, pos, state, isActivated, isMature);
        }
    }
    
    private boolean executeAction(@Nullable PlantActions actions, World worldIn, BlockPos pos, IBlockState state, boolean isActivated, boolean isMature) {
        if (actions == null) return false; // event has no actions
        if (worldIn.isRemote) return true; // event has actions but method is called clientside
        actions.constantAction(worldIn, pos, state);
        actions.activatedAction(worldIn, pos, state);
        actions.matureAction(worldIn, pos, state);
        return true;
    }
    
    public static class PlantActions {
        
        public final PlantAction constantAction;
        public final PlantAction activatedAction;
        public final PlantAction matureAction;
        
        public PlantActions(PlantAction constantAction, PlantAction activatedAction, PlantAction matureAction) {
            this.constantAction = constantAction;
            this.activatedAction = activatedAction;
            this.matureAction = matureAction;
        }
        
        public boolean constantAction(World worldIn, BlockPos pos, IBlockState state) {
            if (this.constantAction == null) return false;
            return this.constantAction.execute(worldIn, pos, state);
        }
        
        public boolean activatedAction(World worldIn, BlockPos pos, IBlockState state) {
            if (this.activatedAction == null) return false;
            return this.constantAction.execute(worldIn, pos, state);
        }
        
        public boolean matureAction(World worldIn, BlockPos pos, IBlockState state) {
            if (this.matureAction == null) return false;
            return this.matureAction.execute(worldIn, pos, state);
        }
        
        public static class PlantAction {
            
            private final String target;
            
            private final int hungerAmount;
            private final int hungerSaturation;
            private final int hungerExhaustion;
            private final int air;
            private final float healthAmount;
            private final int damageType;
            private final int experience;
            private final boolean experienceIsInLevels;
            private final List<PotionEffect> effects;
    
            public PlantAction(String target, int hungerAmount, int hungerSaturation, int hungerExhaustion, int air,
                               float healthAmount, int damageType, int experience, boolean experienceIsInLevels,
                               List<PotionEffect> effects) {
                if (target.startsWith("@s")) throw new IllegalArgumentException("The selector '@s' is not supported");
                if (hungerAmount == 0 && hungerSaturation == 0 && hungerExhaustion == 0 && air == 0 &&
                    healthAmount == 0 && damageType == 0 && experience == 0 && effects.isEmpty())
                    throw new IllegalArgumentException("At least one argument has to be not 0 or not empty.");
                this.target = target;
                this.hungerAmount = hungerAmount;
                this.hungerSaturation = hungerSaturation;
                this.hungerExhaustion = hungerExhaustion;
                this.air = air;
                this.healthAmount = healthAmount;
                this.damageType = damageType;
                this.experience = experience;
                this.experienceIsInLevels = experienceIsInLevels;
                this.effects = effects;
            }
    
            public boolean execute(World worldIn, BlockPos pos, IBlockState state) {
                List<Entity> targets = null;
                try {
                    targets = TargetSelectors.getMatchingEntities(worldIn, pos, state, this.target);
                }
                catch (CommandException e) {
                    // TODO: 06.05.22 misconfiguration
                    return false;
                }
                for (Entity target : targets) {
                    // TODO: 06.05.22 damageType is unused
                    if (target instanceof EntityPlayer) {
                        EntityPlayer entity = (EntityPlayer) target;
                        if (this.hungerAmount != 0 || this.hungerSaturation != 0)
                            entity.getFoodStats().addStats(this.hungerAmount, this.hungerSaturation);
                        // EntityPlayer#addExhaustion has additional logic, so don't change exhaustion via FoodStats#addExhaustion
                        if (this.hungerExhaustion != 0) entity.addExhaustion(this.hungerExhaustion);
                        if (this.air != 0) entity.setAir(entity.getAir() + this.air);
                        if (this.healthAmount != 0) entity.setHealth(entity.getHealth() + this.healthAmount);
                        if (this.experience != 0) {
                            if (experienceIsInLevels) {
                                entity.addExperienceLevel(this.experience);
                            }
                            else {
                                entity.addExperience(this.experience);
                            }
                        }
                        if (!this.effects.isEmpty()) {
                            DebugUtils.releaseMouse(worldIn, true);
                            for (PotionEffect effect : this.effects)
                                entity.addPotionEffect(new PotionEffect(effect));
                        }
                    }
                    else if (target instanceof EntityLivingBase) {
                        EntityLivingBase entity = (EntityLivingBase) target;
                        if (this.air != 0) entity.setAir(entity.getAir() + this.air);
                        if (this.healthAmount != 0) entity.setHealth(entity.getHealth() + this.healthAmount);
                        if (!this.effects.isEmpty())
                            for (PotionEffect effect : this.effects)
                                entity.addPotionEffect(new PotionEffect(effect));
                    }
                    else {
                        if (this.air != 0) target.setAir(target.getAir() + this.air);
                    }
                }
                return true;
            }
        }
    }
}
