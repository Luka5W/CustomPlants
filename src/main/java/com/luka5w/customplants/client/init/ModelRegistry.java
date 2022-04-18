package com.luka5w.customplants.client.init;

import com.luka5w.customplants.common.data.ConfigException;
import com.luka5w.customplants.common.data.Plant;
import com.luka5w.customplants.common.init.PlantsRegistry;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Iterator;

public class ModelRegistry {
    
    private final PlantsRegistry plantsRegistry;
    
    public ModelRegistry(PlantsRegistry plantsRegistry) {
        this.plantsRegistry = plantsRegistry;
        
        MinecraftForge.EVENT_BUS.register(this);
    }
    
    @SubscribeEvent
    public void registerModels(ModelRegistryEvent event) {
        // doesn't help at all, garbage
        Minecraft.getMinecraft().getItemRenderer();
        Minecraft.getMinecraft().getRenderItem();
        Minecraft.getMinecraft().getRenderManager();
        Minecraft.getMinecraft().getResourceManager();
        Minecraft.getMinecraft().getTextureManager();
        Minecraft.getMinecraft().getTextureMapBlocks();
        // default way of registering models
        /*this.plantsRegistry.getBlocks().forEach(it -> {
            ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(it), 0, new ModelResourceLocation(it.getRegistryName(), "inventory"));
        });
        this.plantsRegistry.getItems().forEach(it -> {
            ModelLoader.setCustomModelResourceLocation(it, 0, new ModelResourceLocation(it.getRegistryName(), "inventory"));
        });*/
        // TODO: 18.04.22 complete alternative way: use resource pack and inject it
        //  + textures can be adjusted (overwritten) via resource pack
        //  + i can use the default way (see above)
        //  - how ???
        //  -> see/ search additions++ (how did they realize it)
        //  => how can i inject resource packs without the need that the player has to explicitly enable it?
        
        // TODO: 18.04.22 attempt: use models without json file in the mod's integrated resource pack
        //  + should be(tm) easier
        //  - textures can't be overwritten
        //  - i have to figure out how custom variable models are working
        //  -> read again (?) https://mcforge.readthedocs.io/en/1.12.x/models/introduction/
        //  -> example: water bucket - WHERE???
        Iterator<Plant> plants = this.plantsRegistry.getPlants();
        while (plants.hasNext()) {
            Plant plant = plants.next();
            try {
                Plant.EnumType type = plant.getType();
                plant.getModel(); // required
                plant.getTexture(); // required for: bush, extendable, tree
                plant.getTextures(); // required for: crop, overlay
                // TODO: 18.04.22 load models
            }
            catch (ConfigException e) {
                // TODO: 18.04.22 error handling
            }
        }
    }
}
