package com.luka5w.customplants.common.util.serialization.v0;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.luka5w.customplants.common.util.serialization.JsonUtil;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

import javax.annotation.Nullable;

public class PotionEffectSerializer implements IJsonSerializer<PotionEffect> {
    
    /**
     * Implements an additional null check.
     */
    public static PotionEffect deserializeEffect(JsonElement effectJson, String path, int index)
            throws JsonParseException {
        return deserializeEffect(effectJson, path, String.valueOf(index));
    }
    
    /**
     * Implements an additional null check.
     */
    public static PotionEffect deserializeEffect(JsonElement effectJson, String path, String key)
            throws JsonParseException {
        PotionEffect effect = new PotionEffectSerializer().deserialize(effectJson, path, key);
        if (effect == null) throw JsonUtil.unexpectedType(path);
        return effect;
    }
    
    @Nullable
    @Override
    public PotionEffect deserialize(@Nullable JsonElement json, String path_, String key)
            throws JsonParseException {
        String path = JsonUtil.getPath(path_, key);
        JsonObject effectJson = JsonUtil.getAsT(json, path, JsonElement::getAsJsonObject, "object");
        if (effectJson == null) return null;
        try {
            @SuppressWarnings("ConstantConditions")
            boolean ambient = JsonUtil.getAsT(effectJson.get("ambient"), JsonUtil.getPath(path, "ambient"),
                                              JsonElement::getAsBoolean, "boolean");
            int amplifier = JsonUtil.getAsNumber(effectJson.get("amplifier"), JsonUtil.getPath(path, "amplifier"),
                                                 0, 255, 0);
            int duration = JsonUtil.getAsNumber(effectJson.get("duration"), JsonUtil.getPath(path, "duration"),
                                                0, 1000000, 30); // 30 secs
            int id = JsonUtil.getAsNumber(effectJson.get("id"), JsonUtil.getPath(path, "id"),
                                          0, Integer.MAX_VALUE, -1);
            @SuppressWarnings("ConstantConditions")
            boolean particles = JsonUtil.getAsT(effectJson.get("particles"), JsonUtil.getPath(path, "particles"),
                                                JsonElement::getAsBoolean, "boolean");
            Potion potion = Potion.getPotionById(id);
            return new PotionEffect(
                    potion,
                    duration,
                    amplifier,
                    ambient,
                    particles
            );
        }
        catch (NullPointerException e) {
            throw JsonUtil.unexpectedType(JsonUtil.getPath(path, "*"));
        }
    }
}
