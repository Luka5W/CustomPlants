package com.luka5w.customplants.common.util.serialization;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;

public class JsonUtil {
    
    public static String getPath(@Nullable String path, String key) {
        return path == null ? key : path + "." + key;
    }
    
    public static String getPath(String path, int i) {
        return path + "[" + i + "]";
    }
    
    public static JsonParseException unexpectedType(String path) {
        return new JsonParseException("Expected `" + path + "` to be of not null");
    }
    
    public static JsonParseException unexpectedType(String path, String type) {
        return new JsonParseException("Expected `" + path + "` to be of type " + type);
    }
    
    public static JsonParseException unexpectedRange(String path, Number minInclusive, Number maxInclusive) {
        return new JsonParseException("Expected `" + path + "` to be in range [" + minInclusive + "~" + maxInclusive + "] (inclusive)");
    }
    
    public static JsonParseException excludingValues(String path1, String path2) {
        return new JsonParseException("Unexpected Duplicate: `" + path1 +"` and `" + path1 +"` must not be defined at the same time");
    }
    
    @Nullable
    public static JsonElement getChild(@Nonnull JsonObject json, @Nonnull String key) {
        if (!json.has(key)) return null;
        JsonElement json2 = json.get(key);
        if (json2 == null || json2.isJsonNull()) return null;
        return json2;
    }
    
    @Nullable
    public static <T> T getChildAsT(@Nonnull JsonArray json, String path, int i, ValueGetter<T> g, String type)
            throws JsonParseException {
        return getAsT(json.get(i), JsonUtil.getPath(path, i), g, type);
    }
    
    @Nullable
    public static <T> T getChildAsT(@Nonnull JsonObject json, String path, @Nonnull String key, ValueGetter<T> g, String type)
            throws JsonParseException {
        return getAsT(getChild(json, key), JsonUtil.getPath(path, key), g, type);
    }
    
    @Nullable
    public static <T> T getAsT(@Nullable JsonElement json, String path, ValueGetter<T> g, String type)
            throws JsonParseException {
        if (json == null) return null;
        try {
            return g.get(json);
        }
        catch (IllegalStateException e) {
            throw unexpectedType(path, type);
        }
    }
    
    public static int getAsNumber(@Nullable JsonElement json, String path, int minInclusive, int maxInclusive, int fallback)
            throws JsonParseException {
        Integer val = getAsT(json, path, JsonElement::getAsInt, "integer");
        if (val == null) {
            if (fallback < minInclusive || fallback > maxInclusive) throw unexpectedType(path);
            return fallback;
        }
        if (val < minInclusive || val > maxInclusive) throw unexpectedRange(path, minInclusive, maxInclusive);
        return val;
    }
    public static float getAsNumber(@Nullable JsonElement json, String path, float minInclusive, float maxInclusive, float fallback)
            throws JsonParseException {
        Float val = getAsT(json, path, JsonElement::getAsFloat, "float");
        if (val == null) {
            if (fallback < minInclusive || fallback > maxInclusive) throw unexpectedType(path);
            return fallback;
        }
        if (val < minInclusive || val > maxInclusive) throw unexpectedRange(path, minInclusive, maxInclusive);
        return val;
    }
    public static double getAsNumber(@Nullable JsonElement json, String path, double minInclusive, double maxInclusive, double fallback)
            throws JsonParseException {
        Double val = getAsT(json, path, JsonElement::getAsDouble, "double");
        if (val == null) {
            if (fallback < minInclusive || fallback > maxInclusive) throw unexpectedType(path);
            return fallback;
        }
        if (val < minInclusive || val > maxInclusive) throw unexpectedRange(path, minInclusive, maxInclusive);
        return val;
    }
    
    public static boolean getAsBoolean(@Nullable JsonElement json, String path, boolean fallback) throws JsonParseException {
        Boolean val = getAsT(json, path, JsonElement::getAsBoolean, "boolean");
        return val == null ? fallback : val;
    }
    
    @Nullable
    public static <T> ArrayList<T> getAsArrayOfT(@Nullable JsonElement json, String path, ArrayValueGetter<T> g, String type)
            throws JsonParseException {
        if (json == null) return null;
        JsonArray jsonArray = JsonUtil.getAsT(json, path, JsonElement::getAsJsonArray, "array");
        ArrayList<T> array = new ArrayList<>();
        for (int i = 0; i < jsonArray.size(); i++) {
            try {
                array.add(g.get(jsonArray.get(i), path, i));
            }
            catch (IllegalStateException e) {
                throw new JsonParseException("Expected `" + JsonUtil.getPath(path, i) + "` to be of type " + type);
            }
        }
        return array;
    }
    
    public interface ArrayValueGetter<T> {
        T get(JsonElement e, String path, int i) throws IllegalStateException;
    }
    
    public interface ValueGetter<T> {
        @Nonnull T get(@Nonnull JsonElement e) throws IllegalStateException;
    }
}
