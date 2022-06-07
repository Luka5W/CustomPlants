package com.luka5w.customplants.common.data.plantspacks.plants.serialization.v0;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.luka5w.customplants.common.data.plantspacks.plants.Plant;
import com.luka5w.customplants.common.util.block.material.MaterialLogic;
import com.luka5w.customplants.common.util.serialization.JsonUtil;
import com.luka5w.customplants.common.util.serialization.v0.IJsonSerializer;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.common.EnumPlantType;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Serializes the #plant section of a plant config file.
 * @see <a href="/sketches/custom_plant_v0.json.md">custom_plant_v0.json.md#generic-plants
 */
public class PlantSerializer implements IJsonSerializer<Plant> {
    
    private final boolean canGrow;
    private final int maxDrops;
    
    public PlantSerializer(boolean canGrow, int maxDrops) {
        this.canGrow = canGrow;
        this.maxDrops = maxDrops;
    }
    
    // #
    // plant
    @Nullable
    @Override
    public Plant deserialize(@Nullable JsonElement json, String path_, String key) throws JsonParseException {
        String path = JsonUtil.getPath(path_, key);
        JsonObject plantJson = JsonUtil.getAsT(json, path, JsonElement::getAsJsonObject, "object");
        ArrayList<AxisAlignedBB> aabbs;
        if (this.canGrow || !plantJson.has("bounding_box")) {
            String aabbPath = JsonUtil.getPath(path, "bounding_boxes");
            aabbs = JsonUtil.getAsArrayOfT(JsonUtil.getChild(plantJson, "bounding_boxes"), aabbPath,
                                           (aabb, aabbPath1, aabbI) -> new AABBSerializer(
                                                   JsonUtil.getPath(aabbPath1, aabbI)).deserialize(aabb),
                                           "array<AABB>");
        }
        else {
            String aabbPath = JsonUtil.getPath(path, "bounding_box");
            aabbs = new ArrayList<>();
            aabbs.add(JsonUtil.getAsT(JsonUtil.getChild(plantJson, "bounding_box"),
                                      aabbPath, (aabb) -> new AABBSerializer(aabbPath).deserialize(aabb),
                                      "object/AABB"));
        }
        int burnTime = JsonUtil.getAsNumber(JsonUtil.getChild(plantJson, "burn_time"), JsonUtil.getPath(path, "burn_time"),
                                            0, Integer.MAX_VALUE, 0);
        Material material = new MaterialLogic(MapColor.COLORS[JsonUtil.getAsNumber(JsonUtil.getChild(plantJson, "color"), JsonUtil.getPath(path, "color"), 0, 63, 7)]);
        List<Tuple<Float, ItemStack>> drops = JsonUtil.getAsArrayOfT(JsonUtil.getChild(plantJson, "drops"),
                                                                     JsonUtil.getPath(path, "drops"),
                                                                     (json__, path__, i) -> {
            String dropPath = JsonUtil.getPath(path__, i);
            JsonObject dropJson = JsonUtil.getAsT(json__, dropPath, JsonElement::getAsJsonObject, "object");
            float rounds = JsonUtil.getAsNumber(JsonUtil.getChild(dropJson, "rounds"), JsonUtil.getPath(dropPath, "rounds"), 0, 5, 1);
            int count = JsonUtil.getAsNumber(JsonUtil.getChild(dropJson, "count"), JsonUtil.getPath(dropPath, "count"), 0, Integer.MAX_VALUE, 0);
            String[] item = JsonUtil.getAsT(JsonUtil.getChild(dropJson, "item"), JsonUtil.getPath(dropPath, "item"), JsonElement::getAsString, "string/ResourceLocation").split(":");
            int meta = -1;
            if (item.length == 3) {
                meta = Integer.parseInt(item[2]);
                item[0] += ":" + item[1];
            }
            else if (item.length == 1) {
                try {
                    meta = Integer.parseInt(item[1]);
                    item[0] = "minecraft:" + item[0];
                }
                catch (NumberFormatException e) {
                    item[0] += ":" + item[1];
                }
            }
            return new Tuple<>(rounds, new ItemStack(Item.getByNameOrId(item[0]), count, meta));
                                                                 }, "object");
        List<EnumFacing> facings;
        if (!this.canGrow  || plantJson.has("facings")) {
            facings = JsonUtil.getAsArrayOfT(JsonUtil.getChild(plantJson, "facings"), JsonUtil.getPath(path, "facings"),
                                             (facingJson, facingsPath, i) -> EnumFacing.values()[JsonUtil.getAsNumber(
                                                     facingJson, JsonUtil.getPath(facingsPath, i), 0, 5, 1)],
                                             "array<int[0~5]>");
            
        }
        else {
            facings = new ArrayList<>();
            facings.add(EnumFacing.values()[JsonUtil.getAsNumber(JsonUtil.getChild(plantJson, "facing"),
                                                                 JsonUtil.getPath(path, "facing"), 0, 5, 1)]);
        }
        String oreDict = JsonUtil.getChildAsT(plantJson, path, "ore_dict", JsonElement::getAsString, "string");
        int typeI = JsonUtil.getAsNumber(JsonUtil.getChild(plantJson, "type"), JsonUtil.getPath(path, "type"), -1, 6, EnumPlantType.Plains.ordinal());
        EnumPlantType type = typeI == -1 ? EnumPlantType.Plains : EnumPlantType.values()[typeI];
        boolean soilsEnabled = JsonUtil.getChildAsT(plantJson, path, "soils_enabled", JsonElement::getAsBoolean, "boolean");
        ArrayList<String> soils = null;
        boolean soilsAllowed = false;
        if (soilsEnabled) {
            soils = JsonUtil.getAsArrayOfT(JsonUtil.getChild(plantJson, "soils"), JsonUtil.getPath(path, "soils"),
                                           (soilsJson, _1, _2) -> soilsJson.getAsString(), "array<string>");
            soilsAllowed = JsonUtil.getChildAsT(plantJson, path, "soils_allowed", JsonElement::getAsBoolean, "boolean");
        }
        AxisAlignedBB[] aabbArray;
        if (aabbs == null || aabbs.isEmpty()) {
            aabbArray = new AxisAlignedBB[]{Block.NULL_AABB};
        }
        else  {
            //noinspection ToArrayCallWithZeroLengthArrayArgument
            aabbArray = aabbs.toArray(new AxisAlignedBB[aabbs.size()]);
        }
        return new Plant(aabbArray, burnTime, drops, facings, material, oreDict, type, soils, soilsAllowed);
    }
    
    public static class AABBSerializer {
        
        private final String path;
    
        public AABBSerializer(String path) {
            this.path = path;
        }
        
        public AxisAlignedBB deserialize(JsonElement json) {
            JsonObject aabbJson = JsonUtil.getAsT(json, this.path, JsonElement::getAsJsonObject, "object");
            double x1 = JsonUtil.getAsNumber(JsonUtil.getChild(aabbJson, "x1"), JsonUtil.getPath(this.path, "x1"), 0D, 1D, 0D);
            double y1 = JsonUtil.getAsNumber(JsonUtil.getChild(aabbJson, "y1"), JsonUtil.getPath(this.path, "y1"), 0D, 1D, 0D);
            double z1 = JsonUtil.getAsNumber(JsonUtil.getChild(aabbJson, "z1"), JsonUtil.getPath(this.path, "z1"), 0D, 1D, 0D);
            double x2 = JsonUtil.getAsNumber(JsonUtil.getChild(aabbJson, "x2"), JsonUtil.getPath(this.path, "x2"), 0D, 1D, 1D);
            double y2 = JsonUtil.getAsNumber(JsonUtil.getChild(aabbJson, "y2"), JsonUtil.getPath(this.path, "y2"), 0D, 1D, 1D);
            double z2 = JsonUtil.getAsNumber(JsonUtil.getChild(aabbJson, "z2"), JsonUtil.getPath(this.path, "z2"), 0D, 1D, 1D);
            return new AxisAlignedBB(x1, y1, z1, x2, y2, z2);
        }
    }
}
