package com.luka5w.customplants.common.plantsbehavior;

import com.luka5w.customplants.common.util.TargetSelectors;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class PlantBehavior {
    
    private final ArrayList<Behavior> active;
    private final ArrayList<Behavior> constant;
    private final ArrayList<Behavior> inactive;
    
    public PlantBehavior(ArrayList<Behavior> active, ArrayList<Behavior> constant, ArrayList<Behavior> inactive) {
        this.active = active;
        this.constant = constant;
        this.inactive = inactive;
    }
    
    public void onActivated(boolean activated, int age, World worldIn, BlockPos pos, IBlockState state,
                            EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY,
                            float hitZ) {
        this.executeEvent(worldIn, pos, state, playerIn,
                          this.getEvent(activated, age) == null ? null : this.getEvent(activated, age).clickActions,
                          this.getEvent(age) == null ? null : this.getEvent(age).clickActions);
    }
    
    public void onDestroying(boolean activated, int age, World worldIn, BlockPos pos, IBlockState state,
                             EntityPlayer playerIn) {
        return; // TODO: 05.06.22 is this possible?
    }
    
    public void onDestroyed(boolean activated, int age, World worldIn, BlockPos pos, IBlockState state) {
        this.executeEvent(worldIn, pos, state, null,
                          this.getEvent(activated, age) == null ? null : this.getEvent(activated, age).clickActions,
                          this.getEvent(age) == null ? null : this.getEvent(age).clickActions);
    }
    
    public void onCollidedEntity(boolean activated, int age, World worldIn, BlockPos pos, IBlockState state,
                                 Entity entityIn) {
        this.executeEvent(worldIn, pos, state, entityIn,
                          this.getEvent(activated, age) == null ? null : this.getEvent(activated, age).clickActions,
                          this.getEvent(age) == null ? null : this.getEvent(age).clickActions);
    }
    
    public void onCollidedNPC(boolean activated, int age, World worldIn, BlockPos pos, IBlockState state,
                              EntityLiving entityIn) {
        this.executeEvent(worldIn, pos, state, entityIn,
                          this.getEvent(activated, age) == null ? null : this.getEvent(activated, age).clickActions,
                          this.getEvent(age) == null ? null : this.getEvent(age).clickActions);
    }
    
    public void onCollidedPlayer(boolean activated, int age, World worldIn, BlockPos pos, IBlockState state,
                                 EntityPlayer entityIn) {
        this.executeEvent(worldIn, pos, state, entityIn,
                          this.getEvent(activated, age) == null ? null : this.getEvent(activated, age).clickActions,
                          this.getEvent(age) == null ? null : this.getEvent(age).clickActions);
    }
    
    @Nullable
    private Events getEvent(boolean activated, int age) {
         ArrayList<Behavior> behaviors = (activated ? this.active : this.inactive);
         Behavior behavior = behaviors == null ? null : behaviors.get(age);
         return behavior == null ? null : behavior.actions;
    }
    
    @Nullable
    private Events getEvent(int age) {
        return this.constant.get(age).actions;
    }
    
    private void executeEvent(World worldIn, BlockPos pos, IBlockState state,
                              @Nullable Entity entity, Events.Actions actions,
                              Events.Actions constant) {
        if (actions != null) {
            try {
                actions.execute(constant, worldIn, pos, state, entity);
            }
            catch (Exception e) {
                actions = null;
            }
        }
        else if (constant != null) {
            try {
                constant.execute(constant, worldIn, pos, state, entity);
            }
            catch (Exception e) {
                constant = null;
            }
        }
    }
    
    public boolean hasFeatureEffects() {
        return (this.active != null && this.active.stream().anyMatch(b -> b != null && b.features != null && b.features.hasEffects())) ||
               (this.inactive != null && this.inactive.stream().anyMatch(b -> b != null && b.features != null && b.features.hasEffects())) ||
               this.constant.stream().anyMatch(b -> b.features.hasEffects());
    }
    
    public boolean hasFeatureEffects(boolean activated, int age) {
        Features features = this.getFeatures(activated, age);
        return (features != null && features.hasEffects()) ||
               this.constant.get(age).features.hasEffects();
    }
    
    public List<Tuple<String, PotionEffect>> getFeatureEffects(boolean activated, int age) {
        ArrayList<Tuple<String, PotionEffect>> effects = this.constant.get(age).features.effects;
        Features features = this.getFeatures(activated, age);
        if (features != null && features.effects != null) {
            if (effects == null) return features.effects;
            // don't change - see #removeFeatureEffect
            // results in constant effects first, then (in)active effects
            effects.addAll(features.effects);
        }
        return effects;
    }
    
    public void removeFeatureEffect(boolean activated, int age, int index) {
        int size = this.constant.get(age).features.effects.size();
        // don't change - see #getFeatureEffects
        // assuming constant effects first, then (in)active effects
        if (index < size) {
            this.constant.get(age).features.effects.remove(index);
        }
        else {
            (activated ? this.active : this.inactive).get(age).features.effects.remove(index);
        }
    }
    
    public boolean getBoolFeature(boolean activated, int age, EnumPlantFeature feature) {
        Features features = this.getFeatures(activated, age);
        Features constant = this.constant.get(age).features;
        switch (feature) {
            case CanSustainLeaves:
                return constant.canSustainLeaves || (features != null && features.canSustainLeaves);
            case IsLadder:
                return constant.isLadder || (features != null && features.isLadder);
            case IsSolid:
                return constant.isSolid || (features != null && features.isWeb);
            case IsWeb:
                return constant.isWeb || (features != null && features.isWeb);
            default:
                throw new IllegalStateException("Unknown feature");
        }
    }
    
    public float getFloatFeature(boolean activated, int age, EnumPlantFeature feature) {
        Features features = this.getFeatures(activated, age);
        Features constant = this.constant.get(age).features;
        switch (feature) {
            case GetEnchantPowerBonus:
                if (features == null) return constant.enchantPowerBonus;
                return features.enchantPowerBonus + constant.enchantPowerBonus;
            default:
                throw new IllegalStateException("Unknown feature");
        }
    }
    
    @Nullable
    private Features getFeatures(boolean activated, int age) {
        try {
            return (activated ? this.active : this.inactive).get(age).features;
        }
        catch (NullPointerException e) {
            return null;
        }
    }
    
    public static class Behavior {
        
        private final Events actions;
        private final Features features;
    
        public Behavior(Events actions, Features features) {
            this.actions = actions;
            this.features = features;
        }
    }
    
    public static class Events {
        
        private final Actions clickActions;
        private final Actions collideEntityActions;
        private final Actions collideNPCActions;
        private final Actions collidePlayerActions;
        private final Actions destroyActions;
        private final Actions growActions;
        
        public Events(Actions clickActions, Actions collideEntityActions, Actions collideNPCActions,
                      Actions collidePlayerActions, Events.Actions destroyActions, Events.Actions growActions) {
            this.clickActions = clickActions;
            this.collideEntityActions = collideEntityActions;
            this.collideNPCActions = collideNPCActions;
            this.collidePlayerActions = collidePlayerActions;
            this.destroyActions = destroyActions;
            this.growActions = growActions;
        }
        
        public static class Actions {
            
            private final int air;
            private final ArrayList<PotionEffect> effects;
            private final int xpAmount;
            private final boolean xpAreLevels;
            
            private float health;
            private final int hungerAmount;
            private final int hungerExhaustion;
            private final int hungerSaturation;
            private final String target;
    
            public Actions(int air, ArrayList<PotionEffect> effects, int xpAmount, boolean xpAreLevels, float health, int hungerAmount, int hungerExhaustion, int hungerSaturation,
                           String target) {
                this.air = air;
                this.effects = effects;
                this.xpAmount = xpAmount;
                this.xpAreLevels = xpAreLevels;
                this.health = health;
                this.hungerAmount = hungerAmount;
                this.hungerExhaustion = hungerExhaustion;
                this.hungerSaturation = hungerSaturation;
                this.target = target;
            }
    
            public boolean execute(Actions constant, World worldIn, BlockPos pos,
                                IBlockState state, Entity _entity) throws CommandException {
                List<Entity> targets = TargetSelectors.getMatchingEntities(worldIn, pos, state, this.target);
                for (Entity target : targets) {
                    // TODO: 06.05.22 damageType is unused
                    if (target instanceof EntityPlayer) {
                        EntityPlayer entity = (EntityPlayer) target;
                        if (this.hungerAmount != 0 || this.hungerSaturation != 0)
                            entity.getFoodStats().addStats(this.hungerAmount, this.hungerSaturation);
                        // EntityPlayer#addExhaustion has additional logic, so don't change exhaustion via FoodStats#addExhaustion
                        if (this.hungerExhaustion != 0) entity.addExhaustion(this.hungerExhaustion);
                        if (this.air != 0) entity.setAir(entity.getAir() + this.air);
                        if (this.hungerAmount != 0) entity.setHealth(entity.getHealth() + this.hungerAmount);
                        if (this.xpAmount != 0) {
                            if (this.xpAreLevels) {
                                entity.addExperienceLevel(this.xpAmount);
                            }
                            else {
                                entity.addExperience(this.xpAmount);
                            }
                        }
                        if (!this.effects.isEmpty()) {
                            for (PotionEffect effect : this.effects)
                                entity.addPotionEffect(new PotionEffect(effect));
                        }
                    }
                    else if (target instanceof EntityLivingBase) {
                        EntityLivingBase entity = (EntityLivingBase) target;
                        if (this.air != 0) entity.setAir(entity.getAir() + this.air);
                        if (this.health > 0) {
                            entity.heal(this.health);
                        }
                        if (this.hungerAmount != 0) entity.setHealth(entity.getHealth() + this.hungerAmount);
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
    
    public static class Features {
        
        private final boolean canSustainLeaves;
        private final ArrayList<Tuple<String, PotionEffect>> effects;
        private final float enchantPowerBonus;
        private final boolean isLadder;
        private final boolean isSolid;
        private final boolean isWeb;
        
        public Features(boolean canSustainLeaves, ArrayList<Tuple<String, PotionEffect>> effects, float enchantPowerBonus,
                             boolean isLadder, boolean isSolid, boolean isWeb) {
            this.canSustainLeaves = canSustainLeaves;
            this.effects = effects;
            this.enchantPowerBonus = enchantPowerBonus;
            this.isLadder = isLadder;
            this.isSolid = isSolid;
            this.isWeb = isWeb;
        }
        
        private boolean hasEffects() {
            return this.effects != null && !this.effects.isEmpty();
        }
    }
    
    public enum EnumPlantFeature {
        CanSustainLeaves, GetEnchantPowerBonus, IsLadder, IsSolid, IsWeb;
    }
}
