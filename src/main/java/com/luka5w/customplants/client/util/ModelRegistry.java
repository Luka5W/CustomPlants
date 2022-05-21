package com.luka5w.customplants.client.util;

import com.luka5w.customplants.CustomPlants;
import com.luka5w.customplants.common.util.Registry;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.Logger;


@Mod.EventBusSubscriber(value = Side.CLIENT, modid = CustomPlants.MOD_ID)
public class ModelRegistry {
    
    private static Logger logger;
    
    public static void init(Logger logger) {
        if (ModelRegistry.logger != null) throw new IllegalStateException();
        ModelRegistry.logger = logger;
    }
    
    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        for (Block it : Registry.getInstance().getBlocks()) {
            logger.debug("Registering Models for Block '{}'", it.getRegistryName());
            ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(it), 0,
                                                       new ModelResourceLocation(it.getRegistryName(), "inventory"));
        }
        for (Item it : Registry.getInstance().getItems()) {
            logger.debug("Registering Models for Block '{}'", it.getRegistryName());
            ModelLoader.setCustomModelResourceLocation(it, 0,
                                                       new ModelResourceLocation(it.getRegistryName(), "inventory"));
        }
    }
}
