package com.luka5w.customplants.common.data.plantspacks.plants;

import com.google.gson.*;
import com.luka5w.customplants.common.plantsfeatures.CustomPlantActions;
import com.luka5w.customplants.common.plantsfeatures.CustomPlantFeatures;
import com.luka5w.customplants.common.util.JsonUtils;
import com.luka5w.customplants.common.util.NotInitializedException;
import com.luka5w.customplants.common.util.Registry;
import com.luka5w.customplants.common.util.block.material.MaterialLogic;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.common.EnumPlantType;
import org.apache.commons.lang3.NotImplementedException;

import javax.annotation.Nullable;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Config entries, every plant has in common.
 */
public abstract class PlantConfig {
    
    protected String name;
    
    protected int metaFormat;
    protected String metaVersion;
    /** aka ore dictionary */
    protected String dict;
    protected List<AxisAlignedBB> boundingBoxes;
    protected EnumFacing facing;
    protected Material material;
    protected EnumPlantType type;
    protected boolean soilsEnabled;
    protected boolean soilsAllowed;
    protected List<String> soilsList;
    protected Drop drop;
    protected CustomPlantActions actions;
    protected CustomPlantFeatures features;
    
    protected PlantConfig() {}
    
    /**
     * This method must be called after the class is initialized by Gson.
     * @param name The name (ID) of the plant without (!) prefix (e.g. `minecraft:`).
     * @return `this`
     */
    public PlantConfig finalize(String name) {
        this.name = name;
        return this;
    }
    
    /**
     * This method should be called to before serializing the plant.
     * <p>
     *     <b>Warning</b>: This method assumes that every important value has been initialized in
     *     {@link Serializer#deserialize(PlantConfig, JsonObject, Type, JsonDeserializationContext)}
     * </p>
     * @return Whether finalized is called (i.e. all variables which aren't set by Gson are set) or not.
     */
    public boolean isFinalized() {
        return this.name != null;
    }
    
    /**
     * @throws NotInitializedException When {@link #isFinalized()} returns false.
     */
    public void ensureIsFinalized() throws NotInitializedException {
        if (!isFinalized()) throw new NotInitializedException();
    }
    
    /**
     * If a newer format is available and is current format is compatible with the newer one, the format is updated.
     * @return Whether the format has been updated.
     */
    public boolean updateFormat() {
        // no newer format available
        return false;
    }
    
    /**
     * This method is called so the plant can register all it's blocks and items.
     * @param registry The mod-internal registry where the items are registered to.
     */
    public abstract void addRegistryEntries(Registry registry);
    
    /**
     * (De)Serializes common config entries. Classes which extends to this one (de)serialize their config entries and
     * have to call the super methods {@link #serialize(PlantConfig, Type, JsonSerializationContext)} resp.
     * {@link #deserialize(PlantConfig, JsonObject, Type, JsonDeserializationContext)} first.
     */
    public static abstract class Serializer<T extends PlantConfig> implements JsonDeserializer<T>, JsonSerializer<T> {
        
        /*
         * numbers [x,y,...] only this specific numbers
         * number ranges: [x;y] numbers between the two values (inclusive)
         * number ranges: [x;[ numbers between x and variable size
         */
        
        // section: file/ meta
        private static final String KEY_META_FORMAT = "_format"; // int/enum [0]
        private static final String KEY_META_VERSION = "_version"; // string
        // section: common
        private static final String KEY_0_COMMON = "common"; // object
        private static final String KEY_0_COMMON_DICT = "dict"; // string
        private static final String KEY_0_COMMON_BOUNDING_BOXES = "bounding_boxes"; // array<object/_aabb>
        private static final String[] KEY_0_COMMON_BOUNDING_BOXES_KEYS = {"x1", "y1", "z1", "x2", "y2", "z2"}; // object.keys/_aabb
        private static final String KEY_0_COMMON_FACING = "facing"; // int/enum [0;5]
        private static final String KEY_0_COMMON_COLOR = "color"; // int/enum [0;63]
        private static final String KEY_0_COMMON_TYPE = "type"; // int/enum [-1;6]
        private static final String KEY_0_COMMON_SOILS_ENABLED = "soils_enabled"; // boolean
        private static final String KEY_0_COMMON_SOILS_ALLOWED = "soils_allowed"; // boolean
        private static final String KEY_0_COMMON_SOILS_LIST = "soils"; // array<string/ResourceLocation>
        private static final String KEY_0_COMMON_DROP = "drop"; // object<string/ResourceLocation, object/_drop>
        // section: actions & features (reference: see todo ref!
        private static final String KEY_0_ACTIONS = "actions";
        private static final String KEY_0_FEATURES = "features";
        private static final String[] KEY_0_STATES = {"constant", "activated", "matured"}; // object.keys/_state
        
        // object/_drop
        private static final String KEY_0_DROPS_AMOUNT = "amount"; // int [1;[
        private static final String KEY_0_DROPS_CHANCE = "chance"; // float [0;1]
    
        /**
         * Has to be called last.
         * <p>Checks which version the file to deserialize is and calls the correct version to parse it.</p>
         * @param src An instance of the extending class.
         * @param json The JSON data being deserialized.
         * @param typeOfT The type of the Object to deserialize to.
         * @param context
         * @return The passed instance.
         * @throws JsonParseException If JSON is not in the expected format of typeofT
         * @see JsonDeserializer#deserialize(JsonElement, Type, JsonDeserializationContext)
         */
        public T deserialize(T src, JsonObject json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if (JsonUtils.isJsonPrimitive(json, KEY_META_FORMAT)) {
                try {
                    src.metaFormat = json.get(KEY_META_FORMAT).getAsInt();
                }
                catch(ClassCastException | IllegalStateException e) {
                    throw new JsonParseException("Invalid file format");
                }
                if (src.metaFormat == 0) return this.deserialize0(src, json, typeOfT, context);
                else throw new JsonParseException("Unknown file format");
            }
            else throw new JsonParseException("No or invalid file format");
        }
    
        /**
         * Deserializes the json assuming the file version is 0.
         * @see #deserialize(PlantConfig, JsonObject, Type, JsonDeserializationContext)
         */
        private T deserialize0(T src, JsonObject json, Type typeOfT, JsonDeserializationContext context) {
            src.metaVersion = JsonUtils.getString(json, KEY_META_VERSION);
            JsonObject common = JsonUtils.getJsonObject(json, KEY_0_COMMON);
            JsonArray boundingBoxes = JsonUtils.getJsonArray(common, KEY_0_COMMON_BOUNDING_BOXES, null);
            src.dict = JsonUtils.getString(common, KEY_0_COMMON_DICT, null);
            if (boundingBoxes == null) {
                // when no boundingBoxes are defined, the fallback boundingBox is used (full block)
                src.boundingBoxes = Collections.singletonList(Block.FULL_BLOCK_AABB);
            }
            else {
                src.boundingBoxes = new ArrayList<>();
                for (JsonElement rawBoundingBox : boundingBoxes) {
                    JsonObject boundingBox = rawBoundingBox.getAsJsonObject();
                    int i = 0;
                    src.boundingBoxes.add(new AxisAlignedBB(
                            JsonUtils.getDouble(boundingBox, KEY_0_COMMON_BOUNDING_BOXES_KEYS[i++]),
                            JsonUtils.getDouble(boundingBox, KEY_0_COMMON_BOUNDING_BOXES_KEYS[i++]),
                            JsonUtils.getDouble(boundingBox, KEY_0_COMMON_BOUNDING_BOXES_KEYS[i++]),
                            JsonUtils.getDouble(boundingBox, KEY_0_COMMON_BOUNDING_BOXES_KEYS[i++]),
                            JsonUtils.getDouble(boundingBox, KEY_0_COMMON_BOUNDING_BOXES_KEYS[i++]),
                            JsonUtils.getDouble(boundingBox, KEY_0_COMMON_BOUNDING_BOXES_KEYS[i++])));
                }
            }
            src.facing = EnumFacing.values()[JsonUtils.getInt(common, KEY_0_COMMON_FACING, 1, 0, 5)]; // fallback = UP
            src.material = new MaterialLogic(
                    MapColor.BLOCK_COLORS[JsonUtils.getInt(common, KEY_0_COMMON_COLOR, 7, 0, 63)] // fallback = FOLIAGE
            ).setNoPushMobility();
            int type = JsonUtils.getInt(common, KEY_0_COMMON_TYPE, 0, -1, 6); // fallback = PLAINS
            src.type = type == -1 ? null : EnumPlantType.values()[type];
            src.soilsEnabled = JsonUtils.getBoolean(common, KEY_0_COMMON_SOILS_ENABLED);
            src.soilsAllowed = JsonUtils.getBoolean(common, KEY_0_COMMON_SOILS_ALLOWED);
            src.soilsList = JsonUtils.getListOfStrings(JsonUtils.getJsonArray(common, KEY_0_COMMON_SOILS_LIST));
            src.drop = new Drop(JsonUtils.getJsonObject(common, KEY_0_COMMON_DROP, null), 64);
            src.actions = PlantActions0.deserialize(JsonUtils.getJsonObject(json, KEY_0_ACTIONS));
            src.features = PlantFeatures0.deserialize(JsonUtils.getJsonObject(json, KEY_0_FEATURES));
            
            return src;
        }
    
        /**
         * Has to be called first.
         * <p>Checks which version the config to serialize is and calls the correct version to parse it.</p>
         * @param src The object that needs to be converted to JSON.
         * @param typeOfSrc The actual type (fully genericized version) of the source object.
         * @param context
         * @return The newly created json so the instances of this class can serialize themselves.
         */
        public JsonObject serialize(T src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject json = new JsonObject();
            src.ensureIsFinalized();
            
            json.addProperty(KEY_META_FORMAT, src.metaFormat);
            
            if (src.metaFormat == 0) return this.serialize0(src, json, typeOfSrc, context);
            
            throw new IllegalStateException("Attempted to serialize PlantConfig with unknown format");
        }
    
        /**
         * Serializes the config assuming the version is 0.
         * @see #serialize(PlantConfig, Type, JsonSerializationContext)
         */
        private JsonObject serialize0(T src, JsonObject json, Type typeOfSrc, JsonSerializationContext context) {
            json.addProperty(KEY_META_VERSION, src.metaVersion);
            JsonObject common = new JsonObject();
            if (src.dict != null) common.addProperty(KEY_0_COMMON_DICT, src.dict);
            if (!src.boundingBoxes.isEmpty()) {
                JsonArray boundingBoxes = new JsonArray();
                for (AxisAlignedBB aabb : src.boundingBoxes) {
                    JsonObject boundingBox = new JsonObject();
                    int i = 0;
                    boundingBox.addProperty(KEY_0_COMMON_BOUNDING_BOXES_KEYS[i++], aabb.minX);
                    boundingBox.addProperty(KEY_0_COMMON_BOUNDING_BOXES_KEYS[i++], aabb.minY);
                    boundingBox.addProperty(KEY_0_COMMON_BOUNDING_BOXES_KEYS[i++], aabb.minZ);
                    boundingBox.addProperty(KEY_0_COMMON_BOUNDING_BOXES_KEYS[i++], aabb.maxX);
                    boundingBox.addProperty(KEY_0_COMMON_BOUNDING_BOXES_KEYS[i++], aabb.maxY);
                    boundingBox.addProperty(KEY_0_COMMON_BOUNDING_BOXES_KEYS[i++], aabb.maxZ);
                    boundingBoxes.add(boundingBox);
                }
                common.add(KEY_0_COMMON_BOUNDING_BOXES, boundingBoxes);
            }
            common.addProperty(KEY_0_COMMON_FACING, src.facing.getIndex());
            common.addProperty(KEY_0_COMMON_COLOR, src.material.getMaterialMapColor().colorIndex);
            common.addProperty(KEY_0_COMMON_TYPE, src.type == null ? -1 : src.type.ordinal());
            common.addProperty(KEY_0_COMMON_SOILS_ENABLED, src.soilsEnabled);
            common.addProperty(KEY_0_COMMON_SOILS_ALLOWED, src.soilsAllowed);
            common.add(KEY_0_COMMON_SOILS_LIST, JsonUtils.getArrayFromListOfStrings(src.soilsList));
            common.add(KEY_0_COMMON_DROP, src.drop == null ? new JsonObject() : src.drop.serialize());
            common.add(KEY_0_ACTIONS, PlantActions0.serialize(src.actions));
            common.add(KEY_0_ACTIONS, PlantFeatures0.serialize(src.features));
            
            json.add(KEY_0_COMMON, common);
            
            return json;
        }
        
        private static class PlantActions0 {
    
            private static final String[] KEY_0_ACTIONS_EVENTS = {"collided", "destroyed", "destroying", "activated", "deactivated", "grown", "grown_mature"}; // object.keys/_event
            private static final String KEY_0_ACTIONS_TARGET = "target"; // string/EntitySelector
            private static final String[] KEY_0_ACTIONS_ACTIONS = {
                    "hunger_amount", // int
                    "hunger_saturation", // int
                    "hunger_exhaustion", // int
                    "air", // int
                    "health_amount", // float
                    "damage_type", // int
                    "experience_amount", // int
                    "experience_is_in_levels", // boolean
                    "effects" // array<object/_effect>
            };
            
            private static CustomPlantActions deserialize(JsonObject json) {
                int i = 0;
                CustomPlantActions.PlantActions collisionActions = getActions(json, i++);
                CustomPlantActions.PlantActions destructionActions = getActions(json, i++);
                CustomPlantActions.PlantActions destructingActions = getActions(json, i++);
                CustomPlantActions.PlantActions activationActions = getActions(json, i++);
                CustomPlantActions.PlantActions deactivationActions = getActions(json, i++);
                CustomPlantActions.PlantActions growthActions = getActions(json, i++);
                CustomPlantActions.PlantActions growthMatureActions = getActions(json, i++);
                return new CustomPlantActions(collisionActions, destructionActions, destructingActions, activationActions, deactivationActions, growthActions, growthMatureActions);
            }
            
            @Nullable
            private static CustomPlantActions.PlantActions getActions(JsonObject json, int eventIndex) {
                JsonObject actions = JsonUtils.getJsonObject(json, KEY_0_ACTIONS_EVENTS[eventIndex], null);
                if (actions == null || actions.size() == 0) return null;
                int stateIndex = 0;
                return new CustomPlantActions.PlantActions(
                        getAction(actions, stateIndex++),
                        getAction(actions, stateIndex++),
                        getAction(actions, stateIndex++));
            }
            
            @Nullable
            private static CustomPlantActions.PlantActions.PlantAction getAction(JsonObject actions, int stateIndex) {
                JsonObject action = JsonUtils.getJsonObject(actions, KEY_0_STATES[stateIndex], null);
                if (action == null || action.size() == 0) return null;
                int actionIndex = 0;
                String target = JsonUtils.getString(action, KEY_0_ACTIONS_TARGET, null);
                if (target == null) return null;
                int hungerAmount = JsonUtils.getInt(action, KEY_0_ACTIONS_ACTIONS[actionIndex++], 0);
                int hungerSaturation = JsonUtils.getInt(action, KEY_0_ACTIONS_ACTIONS[actionIndex++], 0);
                int hungerExhaustion = JsonUtils.getInt(action, KEY_0_ACTIONS_ACTIONS[actionIndex++], 0);
                int air = JsonUtils.getInt(action, KEY_0_ACTIONS_ACTIONS[actionIndex++], 0);
                float healthAmount = JsonUtils.getInt(action, KEY_0_ACTIONS_ACTIONS[actionIndex++], 0);
                int damageType = JsonUtils.getInt(action, KEY_0_ACTIONS_ACTIONS[actionIndex++], 0);
                int experience = JsonUtils.getInt(action, KEY_0_ACTIONS_ACTIONS[actionIndex++], 0);
                boolean experienceIsInLevels = JsonUtils.getBoolean(action, KEY_0_ACTIONS_ACTIONS[actionIndex++], false);
                JsonArray effectList = JsonUtils.getJsonArray(action, KEY_0_ACTIONS_ACTIONS[actionIndex++], null);
                List<PotionEffect> effects = effectList.size() == 0 ? Collections.emptyList() : StreamSupport.stream(effectList.spliterator(), false)
                                                                                                             .map(PlantActions0::getEffect)
                                                                                                             .filter(Objects::nonNull)
                                                                                                             .collect(Collectors.toList());
                return new CustomPlantActions.PlantActions.PlantAction(target, hungerAmount, hungerSaturation,
                                                                       hungerExhaustion, air, healthAmount, damageType,
                                                                       experience, experienceIsInLevels, effects);
            }
            
            @Nullable
            private static PotionEffect getEffect(JsonElement element) {
                try {
                    return Effects0.deserialize(element.getAsJsonObject());
                }
                catch (JsonParseException e) {
                    return null;
                }
            }
            
            private static JsonObject serialize(CustomPlantActions actions) {
                // TODO: 06.05.22 implement
                throw new NotImplementedException("Serializing actions is not implemented yet!");
            }
        }
    
        private static class PlantFeatures0 {
    
            private static final String KEY_0_FEATURES_EFFECTS_EFFECT_TARGET = "target";
            private static final String[] KEY_0_FEATURES_FEATURES = {
                    "can_sustain_leaves", // boolean
                    "enchant_power_bonus", // float
                    "is_ladder", // boolean
                    "is_solid", // boolean
                    "is_web", // boolean
                    "effects" // array<object/_effect>
            };
            
            private static CustomPlantFeatures deserialize(JsonObject json) {
                int i = 0;
                return new CustomPlantFeatures(
                        getFeatures(json, i++),
                        getFeatures(json, i++),
                        getFeatures(json, i++));
            }
            
            private static Map<CustomPlantFeatures.EnumPlantFeature, Object> getFeatures(JsonObject json, int stateIndex) {
                JsonObject features;
                try {
                    features = JsonUtils.getJsonObject(json, KEY_0_STATES[stateIndex]);
                } catch (JsonSyntaxException e) {
                    return Collections.emptyMap();
                }
                if (features.size() == 0) return Collections.emptyMap();
                HashMap<CustomPlantFeatures.EnumPlantFeature, Object> map = new HashMap<>();
                int featureIndex = 0;
                addFeature(map, features, featureIndex++, JsonUtils::getBoolean);
                addFeature(map, features, featureIndex++, JsonUtils::getFloat);
                addFeature(map, features, featureIndex++, JsonUtils::getBoolean);
                addFeature(map, features, featureIndex++, JsonUtils::getBoolean);
                addFeature(map, features, featureIndex++, JsonUtils::getBoolean);
                addFeature(map, features, featureIndex++, PlantFeatures0::getEffects);
                return map;
            }
    
            private static <T> void addFeature(HashMap<CustomPlantFeatures.EnumPlantFeature, Object> map,
                                               JsonObject features, int i, Getter<T> getter) {
                T feature;
                try {
                    feature = getter.get(features, KEY_0_FEATURES_FEATURES[i]);
                }
                catch (JsonSyntaxException e) {
                    return;
                }
                if (feature != null) map.put(CustomPlantFeatures.EnumPlantFeature.values()[i], feature);
            }
    
            @Nullable
            private static List<Tuple<PotionEffect, String>> getEffects(JsonObject json, String s) {
                JsonArray effectArray = JsonUtils.getJsonArray(json, s, null);
                if (effectArray == null || effectArray.size() == 0) return null;
                List<Tuple<PotionEffect, String>> effects = new ArrayList<>();
                for (JsonElement jsonElement : effectArray) {
                    JsonObject effect = jsonElement.getAsJsonObject();
                    // fallback: every player inside a cube of a chunk size with the plant in the center.
                    String target = JsonUtils.getString(effect, KEY_0_FEATURES_EFFECTS_EFFECT_TARGET, "@a[x=~-8,y=~-8,z=~-8,dx=16,dy=16,dz=16]");
                    effects.add(new Tuple(Effects0.deserialize(effect), target));
                }
                return effects;
            }
    
            private static JsonObject serialize(CustomPlantFeatures features) {
                // TODO: 06.05.22 implement
                throw new NotImplementedException("Serializing features is not implemented yet!");
            }
    
            private interface Getter<T> {
                T get(JsonObject json, String memberName);
            }
        }
        
        private static class Effects0 {
    
            // object/_effect
            private static final String[] KEY_0_EFFECT = {
                    "effect", // int/enum
                    "duration", // int [0;1000000]
                    "amplifier", // int [0;255]
                    "ambient", // boolean -> true -> beacon; false -> /effect | bottle
                    "particles" // boolean
            }; // object.keys/_effect
            
            private static PotionEffect deserialize(JsonObject json) throws JsonParseException {
                int effectIndex = 0;
                Potion potion = Potion.getPotionById(JsonUtils.getInt(json, KEY_0_EFFECT[effectIndex++], 0, 1, 27));
                if (potion == null) {
                    // TODO: 08.05.22 LOG!
                    return null;
                }
                int duration = JsonUtils.getInt(json, KEY_0_EFFECT[effectIndex++], 0, 0, 1000000);
                int amplifier = JsonUtils.getInt(json, KEY_0_EFFECT[effectIndex++], 0, 0, 255);
                boolean ambient = JsonUtils.getBoolean(json, KEY_0_EFFECT[effectIndex++]);
                boolean showParticles = JsonUtils.getBoolean(json, KEY_0_EFFECT[effectIndex++]);
                return new PotionEffect(potion, duration, amplifier, ambient, showParticles);
            }
            
            private static JsonObject serialize(PotionEffect effect) {
                JsonObject json = new JsonObject();
                int effectIndex = 0;
                json.addProperty(KEY_0_EFFECT[effectIndex++], Potion.getIdFromPotion(effect.getPotion()));
                json.addProperty(KEY_0_EFFECT[effectIndex++], effect.getDuration());
                json.addProperty(KEY_0_EFFECT[effectIndex++], effect.getAmplifier());
                json.addProperty(KEY_0_EFFECT[effectIndex++], effect.getIsAmbient());
                json.addProperty(KEY_0_EFFECT[effectIndex++], effect.doesShowParticles());
                return json;
            }
        }
    }
    
    public static class Drop {
        
        private static final String KEY_0_DROP_NAME = "name";
        private static final String KEY_0_DROP_AMOUNT = "amount";
        private static final String KEY_0_DROP_CHANCE = "chance";
        private final String name;
        private final int amount;
        private final float chance;
        
        public Drop(JsonObject json, int maxAllowed) {
            if (json == null) {
                this.name = null;
                this.amount = 1;
                this.chance = 1;
            }
            else {
                this.name = JsonUtils.getString(json, KEY_0_DROP_NAME);
                this.amount = JsonUtils.getInt(json, KEY_0_DROP_AMOUNT, 1, 0, maxAllowed);
                this.chance = JsonUtils.getFloat(json, KEY_0_DROP_CHANCE, 1, 0, 1);
            }
        }
        
        public JsonObject serialize() {
            JsonObject json = new JsonObject();
            json.addProperty(KEY_0_DROP_NAME, this.name);
            json.addProperty(KEY_0_DROP_AMOUNT, this.amount);
            json.addProperty(KEY_0_DROP_CHANCE, this.chance);
            return json;
        }
    }
}
