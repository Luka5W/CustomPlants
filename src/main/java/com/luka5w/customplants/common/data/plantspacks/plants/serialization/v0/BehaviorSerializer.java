package com.luka5w.customplants.common.data.plantspacks.plants.serialization.v0;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.luka5w.customplants.common.util.serialization.JsonUtil;
import com.luka5w.customplants.common.util.serialization.v0.IJsonSerializer;
import com.luka5w.customplants.common.util.serialization.v0.PotionEffectSerializer;
import com.luka5w.customplants.common.plantsbehavior.PlantBehavior;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.Tuple;

import javax.annotation.Nullable;
import java.util.ArrayList;

/**
 * Serializes the #behavior section of a plant config file.
 * @see <a href="/sketches/custom_plant_v0.json.md">custom_plant_v0.json.md#generic-plants
 */
public class BehaviorSerializer implements IJsonSerializer<PlantBehavior> {
    
    // object/Behavior
    // #
    // behavior
    @Nullable
    @Override
    public PlantBehavior deserialize(@Nullable JsonElement json, String path_, String key) throws JsonParseException {
        // #behavior
        String path = JsonUtil.getPath(path_, key);
        JsonObject behaviorJson = JsonUtil.getAsT(json, path, JsonElement::getAsJsonObject, "object");
        ArrayList<PlantBehavior.Behavior> active = JsonUtil.getAsArrayOfT(JsonUtil.getChild(behaviorJson, "active"),
                                                                          JsonUtil.getPath(path, "active"),
                                                                          BehaviorSerializer::deserializeBehavior,
                                                                          "object/Behavior");
        ArrayList<PlantBehavior.Behavior> constant = JsonUtil.getAsArrayOfT(JsonUtil.getChild(behaviorJson, "constant"),
                                                                            JsonUtil.getPath(path, "constant"),
                                                                            BehaviorSerializer::deserializeBehavior,
                                                                            "object/Behavior");
        ArrayList<PlantBehavior.Behavior> inactive = JsonUtil.getAsArrayOfT(JsonUtil.getChild(behaviorJson, "inactive"),
                                                                            JsonUtil.getPath(path, "inactive"),
                                                                            BehaviorSerializer::deserializeBehavior,
                                                                            "object/Behavior");
        return new PlantBehavior(active, constant, inactive);
    }
    
    // <object/Behavior>
    // #behavior.<condition1>
    // <age>
    public static PlantBehavior.Behavior deserializeBehavior(JsonElement json, String path_, int i) {
        // #behavior.<condition1>[<age>]
        String path = JsonUtil.getPath(path_, i);
        // <object/Behavior>
        JsonObject behaviorJson = JsonUtil.getAsT(json, path, JsonElement::getAsJsonObject, "object");
        if (behaviorJson == null) throw JsonUtil.unexpectedType(path);
        return new PlantBehavior.Behavior(new ActionsSerializer().deserialize(JsonUtil.getChild(behaviorJson, "actions"), path, "actions"),
                                          new FeaturesSerializer().deserialize(
                                                  JsonUtil.getChild(behaviorJson, "features"), path, "features"));
    }
    
    /**
     * Serializes the #behavior.&lt;condition&gt;[&lt;age&gt;].actions section of a plant config file.
     * @see <a href="/sketches/custom_plant_v0.json.md">custom_plant_v0.json.md#objectfeatures</a>
     */
    private static class ActionsSerializer implements IJsonSerializer<PlantBehavior.Events> {
        
        // <object/Events>
        // #behavior.<condition1>[<condition2>]
        // actions
        @Nullable
        @Override
        public PlantBehavior.Events deserialize(@Nullable JsonElement json, String path_, String key)
                throws JsonParseException {
            
            // #behavior.<condition1>[<condition2>].actions
            String path = JsonUtil.getPath(path_, key);
            // <object/Actions>
            JsonObject eventsJson = JsonUtil.getAsT(json, path, JsonElement::getAsJsonObject, "object");
            return eventsJson == null ? null : new PlantBehavior.Events(deserializeActions(eventsJson, path, "clicked"),
                                                                        deserializeActions(eventsJson, path, "collided_entity"),
                                                                        deserializeActions(eventsJson, path, "collided_npc"),
                                                                        deserializeActions(eventsJson, path, "collided_player"),
                                                                        deserializeActions(eventsJson, path, "destroyed"),
                                                                        deserializeActions(eventsJson, path, "grown"));
        }
        
        // <object/Actions>
        // #behavior.<condition1>[<condition2>].actions
        @Nullable
        private PlantBehavior.Events.Actions deserializeActions(JsonObject eventsJson, String path_, String key) {
            // #behavior.<condition1>[<condition2>].actions.<event>
            String path = JsonUtil.getPath(path_, key);
            // <object/Actions>
            JsonObject actionsJson = JsonUtil.getAsT(JsonUtil.getChild(eventsJson, key), path, JsonElement::getAsJsonObject, "object");
            if (actionsJson == null) return null;
            int air = JsonUtil.getAsNumber(JsonUtil.getChild(actionsJson, "air"),
                                           JsonUtil.getPath(path, "air"), -300, 300, 0);
            ArrayList<PotionEffect> effects;
            int xpAmount;
            boolean xpAreLevels;
            float healthAmount;
            int hungerAmount;
            int hungerExhaustion;
            int hungerSaturation;
            String target;
            try {
                effects = JsonUtil.getAsArrayOfT(JsonUtil.getChild(actionsJson, "effects"), JsonUtil.getPath(path, "effects"),
                                                 PotionEffectSerializer::deserializeEffect, "array<object/Effect>");
                xpAmount = JsonUtil.getAsNumber(JsonUtil.getChild(actionsJson, "experience_amount"), JsonUtil.getPath(path, "experience_amount"),
                                                Integer.MIN_VALUE, Integer.MAX_VALUE, 0);
                xpAreLevels = JsonUtil.getAsBoolean(JsonUtil.getChild(actionsJson, "experience_is_in_levels"), JsonUtil.getPath(path, "experience_is_in_levels"),
                                                    false);
                healthAmount = JsonUtil.getAsNumber(JsonUtil.getChild(actionsJson, "health_amount"),
                                                    JsonUtil.getPath(path, "health_amount"), Float.MIN_VALUE, Float.MAX_VALUE, 0F);
                hungerAmount = JsonUtil.getAsNumber(JsonUtil.getChild(actionsJson, "hunger_amount"),
                                                    JsonUtil.getPath(path, "hunger_amount"), -20, 20, 0);
                hungerExhaustion = JsonUtil.getAsNumber(JsonUtil.getChild(actionsJson, "health_exhaustion"), JsonUtil.getPath(path, "health_exhaustion"),
                                                        Integer.MIN_VALUE, Integer.MAX_VALUE, 0);
                //noinspection ConstantConditions
                hungerSaturation = JsonUtil.getChildAsT(actionsJson, path, "health_saturation", JsonElement::getAsInt, "integer");
                target = JsonUtil.getChildAsT(actionsJson, path, "target", JsonElement::getAsString, "string");
            }
            catch (NullPointerException e) {
                throw JsonUtil.unexpectedType(JsonUtil.getPath(path, "*"));
            }
            return new PlantBehavior.Events.Actions(air, effects, xpAmount, xpAreLevels, healthAmount, hungerAmount, hungerExhaustion, hungerSaturation, target);
        }
    }
    
    /**
     * Serializes the #behavior.&lt;condition&gt;[&lt;age&gt;].features section of a plant config file.
     * @see <a href="/sketches/custom_plant_v0.json.md">custom_plant_v0.json.md#objectfeatures</a>
     */
    private static class FeaturesSerializer implements IJsonSerializer<PlantBehavior.Features> {
    
        // <object/Features>
        // #behavior.<condition1>[<condition2>]
        // features
        @Override
        public PlantBehavior.Features deserialize(@Nullable JsonElement json, String path_, String key)
                throws JsonParseException {
            String path = JsonUtil.getPath(path_, key);
            boolean canSustainLeaves;
            ArrayList<Tuple<String, PotionEffect>> effects = null;
            float enchantPowerBonus;
            boolean isLadder;
            boolean isSolid;
            boolean isWeb;
            try {
                JsonObject featuresJson = JsonUtil.getAsT(json, path, JsonElement::getAsJsonObject, "object");
                //noinspection ConstantConditions
                canSustainLeaves = JsonUtil.getChildAsT(featuresJson, path, "can_sustain_leaves", JsonElement::getAsBoolean, "boolean");
                JsonElement effectsJson = JsonUtil.getChild(featuresJson, "effects");
                if (effectsJson != null && !effectsJson.isJsonNull()) effects = JsonUtil.getAsArrayOfT(
                        JsonUtil.getChild(featuresJson, "effects"),
                        JsonUtil.getPath(path, "effects"), (tupleJson, tuplePath, tupleIndex) -> {
                            JsonArray tuple = JsonUtil.getAsT(tupleJson, tuplePath, JsonElement::getAsJsonArray, "array");
                            if (tuple.size() != 2) throw JsonUtil.unexpectedType(tuplePath, "array/tuple[2]");
                            String target = JsonUtil.getChildAsT(tuple, tuplePath, 0, JsonElement::getAsString, "string");
                            PotionEffect effect = PotionEffectSerializer.deserializeEffect(tuple.get(1),
                                                                                           tuplePath, tupleIndex);
                            return new Tuple<>(target, effect);
                        }, "array<tuple<string/TargetSelector, object/Effect>");
                //noinspection ConstantConditions
                enchantPowerBonus = JsonUtil.getChildAsT(featuresJson, path, "enchant_power_bonus", JsonElement::getAsFloat, "float");
                //noinspection ConstantConditions
                isLadder = JsonUtil.getChildAsT(featuresJson, path, "is_ladder", JsonElement::getAsBoolean, "boolean");
                //noinspection ConstantConditions
                isSolid = JsonUtil.getChildAsT(featuresJson, path, "is_solid", JsonElement::getAsBoolean, "boolean");
                //noinspection ConstantConditions
                isWeb = JsonUtil.getChildAsT(featuresJson, path, "is_web", JsonElement::getAsBoolean, "boolean");
            }
            catch (NullPointerException e) {
                throw JsonUtil.unexpectedType(JsonUtil.getPath(path, "*"));
            }
            return new PlantBehavior.Features(canSustainLeaves, effects, enchantPowerBonus, isLadder, isSolid, isWeb);
        }
    }
}
