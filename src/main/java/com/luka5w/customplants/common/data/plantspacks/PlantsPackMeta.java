package com.luka5w.customplants.common.data.plantspacks;

import com.google.gson.*;
import com.luka5w.customplants.common.util.serialization.old.JsonUtils;

import java.lang.reflect.Type;
import java.util.List;

public class PlantsPackMeta {
    
    private String rpDescription;
    private int rpFormat;
    
    private final String ppID;
    private String ppVersion;
    private int ppFormat;
    private List<String> ppRequiredPacks;
    private List<String> ppRequiredMods;
    
    public String getDescription() {
        return rpDescription;
    }
    
    public int getRPFormat() {
        return rpFormat;
    }
    
    public String getID() {
        return ppID;
    }
    
    public String getVersion() {
        return ppVersion;
    }
    
    public int getFormat() {
        return ppFormat;
    }
    
    public List<String> getRequiredPacks() {
        return ppRequiredPacks;
    }
    
    public List<String> getRequiredMods() {
        return ppRequiredMods;
    }
    
    private PlantsPackMeta(String rpDescription, int rpFormat, String ppID, String ppVersion, int ppFormat, List<String> ppRequiredPacks, List<String> ppRequiredMods) {
        this.ppID = ppID;
        this.rpDescription = rpDescription;
        this.rpFormat = rpFormat;
        this.ppVersion = ppVersion;
        this.ppFormat = ppFormat;
        this.ppRequiredPacks = ppRequiredPacks;
        this.ppRequiredMods = ppRequiredMods;
    }
    
    public static class Serializer implements JsonDeserializer<PlantsPackMeta>, JsonSerializer<PlantsPackMeta> {
        
        private static final String KEY_RESOURCE_PACK = "pack";
        private static final String KEY_PLANTS_PACK = "customplants";
        private static final String KEY_RP_DESCRIPTION = "description";
        private static final String KEY_RP_FORMAT = "pack_format";
        private static final String KEY_PP_ID = "id";
        private static final String KEY_PP_VERSION = "version";
        private static final String KEY_PP_FORMAT = "pack_format";
        private static final String KEY_PP_PACKS = "required_packs";
        private static final String KEY_PP_MODS = "required_mods";
    
        @Override
        public PlantsPackMeta deserialize(JsonElement element, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            JsonObject json = element.getAsJsonObject();
            // ResourcePack
            JsonObject rp = JsonUtils.getJsonObject(json, KEY_RESOURCE_PACK);
            String rpDescription = JsonUtils.getString(rp, KEY_RP_DESCRIPTION);
            int rpFormat = JsonUtils.getInt(rp, KEY_RP_FORMAT);
            
            // PlantsPack
            JsonObject pp = JsonUtils.getJsonObject(json, KEY_PLANTS_PACK);
            String ppID = JsonUtils.getString(pp, KEY_PP_ID);
            String ppVersion = JsonUtils.getString(pp, KEY_PP_VERSION);
            int ppFormat = JsonUtils.getInt(pp, KEY_PP_FORMAT);
            List<String> ppPacks = JsonUtils.getListOfStrings(JsonUtils.getJsonArray(pp, KEY_PP_PACKS));
            List<String> ppMods = JsonUtils.getListOfStrings(JsonUtils.getJsonArray(pp, KEY_PP_MODS));
            return new PlantsPackMeta(rpDescription, rpFormat, ppID, ppVersion, ppFormat, ppPacks, ppMods);
        }
    
        @Override
        public JsonElement serialize(PlantsPackMeta src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject json = new JsonObject();
            
            // ResourcePack
            JsonObject rp = new JsonObject();
            rp.addProperty(KEY_RP_DESCRIPTION, src.rpDescription);
            rp.addProperty(KEY_RP_FORMAT, src.rpFormat);
            json.add(KEY_RESOURCE_PACK, rp);
            
            // PlantsPack
            JsonObject pp = new JsonObject();
            pp.addProperty(KEY_PP_VERSION, src.ppVersion);
            pp.addProperty(KEY_PP_FORMAT, src.ppFormat);
            JsonArray ppPacks = JsonUtils.getArrayFromListOfStrings(src.ppRequiredPacks);
            pp.add(KEY_PP_PACKS, ppPacks);
            JsonArray ppMods = JsonUtils.getArrayFromListOfStrings(src.ppRequiredMods);
            pp.add(KEY_PP_MODS, ppMods);
            json.add(KEY_PLANTS_PACK, pp);
            
            return json;
        }
    }
}
