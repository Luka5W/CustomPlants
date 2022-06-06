package com.luka5w.customplants.common.data.plantspacks.plants.serialization;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.luka5w.customplants.common.data.plantspacks.plants.PlantsFileMeta;
import com.luka5w.customplants.common.util.serialization.v0.IJsonSerializer;
import com.luka5w.customplants.common.util.serialization.JsonUtil;

import javax.annotation.Nullable;

/**
 * Serializes the #_file section of a plant config file
 * @see <a href="/sketches/custom_plant_v0.json.md">custom_plant_v0.json.md#generic-plants
 */
public class FileSerializer implements IJsonSerializer<PlantsFileMeta> {
    
    // #
    // _file
    @Nullable
    @Override
    public PlantsFileMeta deserialize(@Nullable JsonElement json, String path_, String key) throws JsonParseException {
        String path = JsonUtil.getPath(path_, key);
        JsonObject fileJson = JsonUtil.getAsT(json, path, JsonElement::getAsJsonObject, "object");
        int format = JsonUtil.getAsNumber(JsonUtil.getChild(fileJson, "format"), JsonUtil.getPath(path, "format"), 0, 0, -1);
        String version = JsonUtil.getChildAsT(fileJson, path, "version", JsonElement::getAsString, "string");
        return new PlantsFileMeta(format, version);
    }
}
