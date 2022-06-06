package com.luka5w.customplants.common.data.plantspacks.plants.serialization;

import com.google.gson.*;
import com.luka5w.customplants.common.data.plantspacks.plants.Config;
import com.luka5w.customplants.common.data.plantspacks.plants.PlantsFileMeta;
import com.luka5w.customplants.common.data.plantspacks.plants.Type;
import com.luka5w.customplants.common.data.plantspacks.plants.serialization.v0.BehaviorSerializer;
import com.luka5w.customplants.common.data.plantspacks.plants.serialization.v0.PlantSerializer;
import com.luka5w.customplants.common.data.plantspacks.plants.serialization.v0.TypeSerializer;
import com.luka5w.customplants.common.util.serialization.JsonUtil;

/**
 * Main serializer for plant configs.
 * @see <a href="/sketches/custom_plant_v0.json.md">custom_plant_v0.json.md#generic-plants
 */
public class Serializer implements JsonDeserializer<Config> {
    
    private final Type.EnumType type;
    private final int maxDrops;
    
    public Serializer(Type.EnumType type, int maxDrops) {
        this.type = type;
        this.maxDrops = maxDrops;
    }
    
    @Override
    public Config deserialize(JsonElement json, java.lang.reflect.Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        String rootPath = "#";
        JsonObject rootJson = JsonUtil.getAsT(json, rootPath, JsonElement::getAsJsonObject, "object");
        PlantsFileMeta _file = new FileSerializer().deserialize(JsonUtil.getChild(rootJson, "_file"), "#", "_file");
        switch (_file.getFormat()) {
            case 0:
                boolean canGrow = this.type == Type.EnumType.Crops ||
                                  this.type == Type.EnumType.Overlay;
                return new Config(
                        _file,
                        new BehaviorSerializer().deserialize(JsonUtil.getChild(rootJson, "behavior"), rootPath, "behavior"),
                        new PlantSerializer(canGrow, this.maxDrops).deserialize(JsonUtil.getChild(rootJson, "plant"), rootPath, "plant"),
                        new TypeSerializer(this.type).deserialize(JsonUtil.getChild(rootJson, "type"), rootPath, "type")
                );
            default:
                throw JsonUtil.unexpectedRange("#._file.format", 0, 0);
        }
    }
}
