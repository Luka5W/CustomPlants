package com.luka5w.customplants.common.util.serialization.v0;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import javax.annotation.Nullable;

public interface IJsonSerializer<T> {
    
    /**
     * Deserializes a json element to
     * @param json The json to deserialize
     * @param path The path to the element where json is in
     * @param key The key of json
     * @return The deserialized json
     * @throws JsonParseException
     */
    @Nullable
    T deserialize(@Nullable JsonElement json, String path, String key) throws JsonParseException;
}
