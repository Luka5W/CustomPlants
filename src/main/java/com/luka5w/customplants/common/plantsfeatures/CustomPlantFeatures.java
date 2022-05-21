package com.luka5w.customplants.common.plantsfeatures;

import net.minecraft.potion.PotionEffect;
import net.minecraft.util.Tuple;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CustomPlantFeatures {
    
    private final Map<EnumPlantFeature, Object> constantFeatures;
    private final Map<EnumPlantFeature, Object> activeFeatures;
    private final Map<EnumPlantFeature, Object> maturedFeatures;
    
    public CustomPlantFeatures(Map<EnumPlantFeature, Object> constantFeatures,
                               Map<EnumPlantFeature, Object> activeFeatures,
                               Map<EnumPlantFeature, Object> maturedFeatures) {
        this.constantFeatures = constantFeatures;
        this.activeFeatures = activeFeatures;
        this.maturedFeatures = maturedFeatures;
    }
    
    public boolean getBoolFeature(boolean isActivated, boolean isMature, EnumPlantFeature feature, boolean or) {
        if (or) return getValue(true, this.constantFeatures, feature, Boolean.class, false) ||
                       getValue(isActivated, this.activeFeatures, feature, Boolean.class, false) ||
                       getValue(isMature, this.maturedFeatures, feature, Boolean.class, false);
        return getValue(true, this.constantFeatures, feature, Boolean.class, false) &&
               getValue(isActivated, this.activeFeatures, feature, Boolean.class, false) &&
               getValue(isMature, this.maturedFeatures, feature, Boolean.class, false);
    }
    
    public float getFloatFeature(boolean isActivated, boolean isMatured, EnumPlantFeature feature, EnumNumberFilter filter) {
        float constant = getValue(true, this.constantFeatures, feature, Float.class, 0.0f);
        float active = getValue(isActivated, this.activeFeatures, feature, Float.class, 0.0f);
        float mature = getValue(isMatured, this.maturedFeatures, feature, Float.class, 0.0f);
        if (filter == EnumNumberFilter.Sum) return constant + active + mature;
        if (filter == EnumNumberFilter.Highest) return Math.max(Math.max(constant, active), mature);
        if (filter == EnumNumberFilter.Lowest) return Math.min(Math.min(constant, active), mature);
        throw new IllegalStateException("Unknown EnumNumberFilter");
    }
    
    @Nullable
    public List<Tuple<PotionEffect, String>> getEffects(boolean isActivated, boolean isMatured) {
        ArrayList<Tuple<PotionEffect, String>> constantEffects = getValue(true, this.constantFeatures, EnumPlantFeature.Effects, ArrayList.class, null);
        ArrayList<Tuple<PotionEffect, String>> activeEffects = getValue(isActivated, this.activeFeatures, EnumPlantFeature.Effects, ArrayList.class, null);
        ArrayList<Tuple<PotionEffect, String>> maturedEffects = getValue(isMatured, this.maturedFeatures, EnumPlantFeature.Effects, ArrayList.class, null);
        if ((constantEffects == null || constantEffects.isEmpty()) &&
            (activeEffects == null || activeEffects.isEmpty()) &&
            (maturedEffects == null || maturedEffects.isEmpty())) return null;
        return Stream.of(constantEffects, activeEffects, maturedEffects)
                                                          .filter(Objects::nonNull)
                                                          .flatMap(Collection::stream)
                                                          .collect(Collectors.toList());
                
    }
    
    public boolean hasEffects() {
        return this.hasEffects(this.constantFeatures) || this.hasEffects(this.activeFeatures) || this.hasEffects(this.maturedFeatures);
    }
    private boolean hasEffects(Map<EnumPlantFeature, Object> map) {
        if (!map.containsKey(EnumPlantFeature.Effects)) return false;
        List effects = (List) map.get(EnumPlantFeature.Effects);
        return effects != null && !effects.isEmpty();
    }
    
    private static <T> T getValue(boolean isInState, Map<EnumPlantFeature, Object> map, EnumPlantFeature feature, Class<T> clazz, T fallback) {
        if (isInState && map.containsKey(feature)) {
            Object v = map.get(feature);
            if (v == null) return fallback;
            if (v.getClass().equals(clazz)) return (T) v;
            throw new IllegalStateException("Illegal Feature Mapping: expected " + clazz.getName() + ", got " + v.getClass().getName());
        }
        return fallback;
    }
    
    public enum EnumPlantFeature {
        CanSustainLeaves,
        GetEnchantPowerBonus,
        IsLadder,
        IsSolid,
        IsWeb,
        Effects
    }
    
    public enum EnumNumberFilter {
        Sum,
        Highest,
        Lowest;
    }
}
