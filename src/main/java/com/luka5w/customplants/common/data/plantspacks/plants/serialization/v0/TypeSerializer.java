package com.luka5w.customplants.common.data.plantspacks.plants.serialization.v0;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.luka5w.customplants.common.data.plantspacks.plants.Type;
import com.luka5w.customplants.common.util.serialization.JsonUtil;
import com.luka5w.customplants.common.util.serialization.v0.IJsonSerializer;
import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Set;

public class TypeSerializer implements IJsonSerializer<Type> {
    
    private final Type.EnumType type;
    
    public TypeSerializer(Type.EnumType type) {
        this.type = type;
    }
    
    // #
    // type
    @Nullable
    @Override
    public Type deserialize(@Nullable JsonElement json, String path_, String key) throws JsonParseException {
        // #.behavior
        String path = JsonUtil.getPath(path_, key);
        JsonObject typeJson = JsonUtil.getAsT(json, path, JsonElement::getAsJsonObject, "object");
        boolean canUseBonemeal;
        int reqLightLvl;
        switch (this.type) {
            case Crops:
                if (typeJson == null || typeJson.isJsonNull()) throw JsonUtil.unexpectedType(path);
                canUseBonemeal = JsonUtil.getAsBoolean(JsonUtil.getChild(typeJson, "can_use_bonemeal"),
                                                               JsonUtil.getPath(path, "can_use_bonemeal"), true);
                reqLightLvl = JsonUtil.getAsNumber(JsonUtil.getChild(typeJson, "min_light_lvl"),
                                                   JsonUtil.getPath(path, "min_light_lvl"), 0, 15, 9);
                String oreDict = JsonUtil.getChildAsT(typeJson, path, "ore_dict", JsonElement::getAsString,
                                                      "string");
                return new Type.Crops(this.type, canUseBonemeal, oreDict, reqLightLvl);
            case Sapling:
                if (typeJson == null || typeJson.isJsonNull()) throw JsonUtil.unexpectedType(path);
                canUseBonemeal = JsonUtil.getAsBoolean(JsonUtil.getChild(typeJson, "can_use_bonemeal"),
                                                               JsonUtil.getPath(path, "can_use_bonemeal"), true);
                reqLightLvl = JsonUtil.getAsNumber(JsonUtil.getChild(typeJson, "min_light_lvl"),
                                                       JsonUtil.getPath(path, "min_light_lvl"), 0, 15, 9);
                ResourceLocation logName = this.getResourceLocation(JsonUtil.getChild(typeJson, "log"),
                                                                    JsonUtil.getPath(path, "log"));
                ResourceLocation leafName = this.getResourceLocation(JsonUtil.getChild(typeJson, "leaf"),
                                                                     JsonUtil.getPath(path, "leaf"));
                Set<Map.Entry<ResourceLocation, Block>> blocks = ForgeRegistries.BLOCKS.getEntries();
                Block logBlock = null;
                Block leafBlock = null;
                for (Map.Entry<ResourceLocation, Block> block : blocks) {
                    if (block.getKey().equals(logName)) logBlock = block.getValue();
                    if (block.getKey().equals(leafName)) leafBlock = block.getValue();
                }
                if (logBlock == null) throw new JsonParseException("Unable to find log block with name " + logName);
                if (leafBlock == null) throw new JsonParseException("Unable to find log block with name " + leafName);
                return new Type.Sapling(this.type, canUseBonemeal, reqLightLvl, logBlock, leafBlock);
            default:
                return new Type(this.type);
        }
    }
    
    @Nullable
    private ResourceLocation getResourceLocation(JsonElement json, String path) {
        return JsonUtil.getAsT(json, path, it -> {
                    String[] res = it.getAsString().split(":");
                    if (res.length == 1) return new ResourceLocation(res[0]);
                    if (res.length == 2) return new ResourceLocation(res[0], res[1]);
                    if (res.length == 3) throw new JsonParseException("state is currently not supported");
                    throw new IllegalStateException();
                }, "string/ResourceLocation");
    }
}
