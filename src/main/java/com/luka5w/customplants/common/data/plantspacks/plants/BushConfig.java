package com.luka5w.customplants.common.data.plantspacks.plants;

import com.google.gson.*;
import com.luka5w.customplants.common.blocks.BlockCustomBush;
import com.luka5w.customplants.common.tileentities.TileEntityCustomBush;
import com.luka5w.customplants.common.util.JsonUtils;
import com.luka5w.customplants.common.util.Registry;
import net.minecraft.block.Block;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import java.lang.reflect.Type;

public class BushConfig extends PlantConfig {
    
    protected int burnTime;
    
    @Override
    public void addRegistryEntries(Registry registry) {
        Block block = this.soilsEnabled ? new BlockCustomBush(
                this.boundingBoxes.get(0),
                BlockFaceShape.CENTER,
                this.facing,
                this.material,
                this.type,
                this.actions,
                this.features,
                this.soilsAllowed,
                this.soilsList
        ) : new BlockCustomBush(
                this.boundingBoxes.get(0),
                BlockFaceShape.CENTER,
                this.facing,
                this.material,
                this.type,
                this.actions,
                this.features
        );
        registry.addBlock(block, this.name, false, registry.getDefaultTab());
        registry.addItemBlock(new ItemBlock(block) {
            @Override
            public int getItemBurnTime(ItemStack itemStack) {
                return burnTime;
            }
        }, this.name);
        registry.addTileEntity(TileEntityCustomBush.class, this.name);
    }
    
    public static class Serializer extends PlantConfig.Serializer<BushConfig> {
    
        private static final String KEY_0_BUSH = "bush";
        private static final String KEY_0_BUSH_BURN_TIME = "burn_time";
    
        @Override
        public BushConfig deserialize(JsonElement element, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject json = element.getAsJsonObject();
            BushConfig config = super.deserialize(new BushConfig(), json, typeOfT, context);;
            
            if (config.metaFormat == 0) return this.deserialize0(config, json, typeOfT, context);
    
            throw new IllegalStateException("Attempted to deserialize PlantConfig with unknown format");
        }
        
        
        private BushConfig deserialize0(BushConfig config, JsonObject json, Type typeOfT, JsonDeserializationContext context) {
            JsonObject bush = JsonUtils.getJsonObject(json, KEY_0_BUSH);
            
            config.burnTime = JsonUtils.getInt(bush, KEY_0_BUSH_BURN_TIME);
            
            return config;
        }
    
        @Override
        public JsonObject serialize(BushConfig src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject json = super.serialize(src, typeOfSrc, context);
            
            if (src.metaFormat == 0) return this.serialize0(src, json, typeOfSrc, context);
    
            throw new IllegalStateException("Attempted to serialize PlantConfig with unknown format");
        }
        
        private JsonObject serialize0(BushConfig src, JsonObject json, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject bush = new JsonObject();
            
            bush.addProperty(KEY_0_BUSH_BURN_TIME, src.burnTime);
            
            json.add(KEY_0_BUSH, bush);
            return json;
        }
    }
}
