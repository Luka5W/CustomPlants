package com.luka5w.customplants.common.util;

import com.google.gson.*;

import java.util.*;

public class JsonUtils extends net.minecraft.util.JsonUtils {
    
    /**
     * Throws an exception when the passed json element is not the expected type.
     * @param json The element to test.
     * @param expected The expected class.
     * @param keys The keys (for logging).
     * @throws JsonParseException When the types mismatch.
     */
    public static void assertType(JsonElement json, Class<? extends JsonElement> expected, String... keys) {
        if (!json.getClass().equals(expected)) throw new JsonParseException("Expected '" + String.join(".", keys) + "to be of type '" + expected.getName() + "' got '" + json.getClass().getName() + "'");
    }
    
    // stolen from net.minecraft.util.JsonUtils#getFloat
    public static double getDouble(JsonElement json, String memberName) {
        if (json.isJsonPrimitive() && json.getAsJsonPrimitive().isNumber()) {
            return json.getAsDouble();
        }
        else {
            throw new JsonSyntaxException("Expected " + memberName + " to be a Double, was " + toString(json));
        }
    }
    
    // stolen from net.minecraft.util.JsonUtils#getFloat
    public static double getDouble(JsonObject json, String memberName) {
        if (json.has(memberName)) {
            return getDouble(json.get(memberName), memberName);
        }
        else {
            throw new JsonSyntaxException("Missing " + memberName + ", expected to find a Double");
        }
    }
    
    // stolen from net.minecraft.util.JsonUtils#getFloat
    public static double getDouble(JsonObject json, String memberName, float fallback) {
        return json.has(memberName) ? getDouble(json.get(memberName), memberName) : fallback;
    }
    
    /**
     * Gets an integer using {@link JsonUtils#getInt(JsonObject, String, int)} and check if its in range.
     * @param json The object to get the member from.
     * @param memberName The name of the member.
     * @param fallback The fallback value to use if the member does not exist.
     * @param min The minimum value (including) of the members value.
     * @param max The maximum value (including) of the members value.
     * @return The value of the member.
     * @throws JsonParseException ({@link IndexOutOfBoundsException}) When the value is out of range.
     */
    public static int getInt(JsonObject json, String memberName, int fallback, int min, int max) {
        int i = getInt(json, memberName, fallback);
        if (i < min || i > max) throwIndexOutOfBoundsException(memberName, min, max);
        return i;
    }
    
    /**
     * Gets an integer using {@link JsonUtils#getInt(JsonObject, String)} and check if its in range.
     * @param json The object to get the member from.
     * @param memberName The name of the member.
     * @param min The minimum value (including) of the members value.
     * @param max The maximum value (including) of the members value.
     * @return The value of the member.
     * @throws JsonParseException ({@link IndexOutOfBoundsException}) When the value is out of range.
     */
    public static int getInt(JsonObject json, String memberName, int min, int max) {
        int i = getInt(json, memberName);
        if (i < min || i > max) throwIndexOutOfBoundsException(memberName, min, max);
        return i;
    }
    
    /**
     * Gets a float using {@link JsonUtils#getFloat(JsonObject, String, float)} and check if its in range.
     * @param json The object to get the member from.
     * @param memberName The name of the member.
     * @param fallback The fallback value to use if the member does not exist.
     * @param min The minimum value (including) of the members value.
     * @param max The maximum value (including) of the members value.
     * @return The value of the member.
     * @throws JsonParseException ({@link IndexOutOfBoundsException}) When the value is out of range.
     */
    public static float getFloat(JsonObject json, String memberName, float fallback, float min, float max) {
        float i = getFloat(json, memberName, fallback);
        if (i < min || i > max) throwIndexOutOfBoundsException(memberName, min, max);
        return i;
    }
    
    public static void throwIndexOutOfBoundsException(String memberName, Number min, Number max) {
        throw new JsonParseException(new IndexOutOfBoundsException("Field " + memberName + " has to be in range (including) [" + min + "..." + max + "]"));
    }
    
    /**
     * Serializes an array of Strings.
     * @param array The array to serialize.
     * @return The serialized array.
     */
    public static JsonArray getArrayFromListOfStrings(List<String> array) {
        JsonArray json = new JsonArray();
        for (String id : array) json.add(id);
        return json;
    }
    
    /**
     * Deserializes an array of Strings.
     * @param json The array to deserialize.
     * @return The deserialized array.
     * @throws JsonParseException When {@link JsonElement#getAsString()} throws a {@link ClassCastException} or {@link IllegalStateException}.
     */
    public static List<String> getListOfStrings(JsonArray json) throws JsonParseException {
        if (json.size() == 1) return Arrays.asList(json.getAsString());
        ArrayList<String> array = new ArrayList<>();
        if (json.size() == 0) return array;
        try {
            for (JsonElement el : json) array.add(el.getAsString());
        }
        catch (ClassCastException | IllegalStateException e) {
            throw new JsonParseException(e);
        }
        return array;
    }
    
    /**
     * Serializes a map with Float values.
     * @param map The map to serialize.
     * @return The serialized map.
     */
    public static JsonObject getObjectFromMapOfFloats(Map<String, Float> map) {
        JsonObject json = new JsonObject();
        Set<Map.Entry<String, Float>> set = map.entrySet();
        for (Map.Entry<String, Float> entry : set) json.addProperty(entry.getKey(), entry.getValue());
        return json;
    }
    
    /**
     * Deserializes an object with values of any kind.
     * @param json The object to deserialize.
     * @param getter The deserialization method for each value.
     * @return The deserialized object.
     * @param <V> The type of the values.
     */
    public static <V> HashMap<String, V> getMap(JsonObject json, Getter<V> getter) {
        HashMap<String, V> map = new HashMap<>();
        if (json.size() == 0) return map;
        Set<Map.Entry<String, JsonElement>> set = json.entrySet();
        try {
            for (Map.Entry<String, JsonElement> el : set) map.put(el.getKey(), getter.get(el.getValue()));
        }
        catch (UnsupportedOperationException | ClassCastException | IllegalStateException | NumberFormatException e) {
            throw new JsonParseException(e);
        }
        return map;
    }
    
    /**
     * A functional interface for {@link #getMap(JsonObject, Getter)}
     * @param <V> The value of the map.
     */
    public interface Getter<V> {
        /**
         * @param element The element to get the value from.
         * @return The value of the element.
         * @throws UnsupportedOperationException May be thrown by Gson while getting the value.
         * @throws ClassCastException May be thrown by Gson while getting the value.
         * @throws IllegalStateException May be thrown by Gson while getting the value.
         * @throws NumberFormatException May be thrown by Gson while getting the value.
         */
        V get(JsonElement element) throws UnsupportedOperationException, ClassCastException, IllegalStateException, NumberFormatException;
    }
}
