package com.luka5w.customplants.common.data.plantspacks.plants.serialization.v0;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.luka5w.customplants.common.data.plantspacks.plants.Type;
import com.luka5w.customplants.common.util.serialization.JsonUtil;
import com.luka5w.customplants.common.util.serialization.v0.IJsonSerializer;

import javax.annotation.Nullable;

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
        switch (this.type) {
            case Crops:
                boolean canUseBonemeal = JsonUtil.getAsBoolean(JsonUtil.getChild(typeJson, "can_use_bonemeal"),
                                                               JsonUtil.getPath(path, "can_use_bonemeal"), true);
                return new Type.Crops(this.type, canUseBonemeal);
            default:
                return new Type(this.type);
        }
    }
}
