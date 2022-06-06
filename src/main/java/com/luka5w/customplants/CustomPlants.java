package com.luka5w.customplants;

import com.luka5w.customplants.common.CommonProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;

@Mod(modid = CustomPlants.MOD_ID, useMetadata = true, updateJSON = "https://raw.githubusercontent.com/Luka5W/CustomPlants/dev/versions.json")
public class CustomPlants {

    public static final String MOD_ID = "customplants";
    private static Logger logger;
    @Instance(MOD_ID)
    private static CustomPlants instance;
    @SidedProxy(clientSide = "com.luka5w.customplants.client.ClientProxy", serverSide = "com.luka5w.customplants.common.CommonProxy")
    private static CommonProxy proxy;
    
    public static CustomPlants getInstance() {
        return instance;
    }
    
    public static CommonProxy getProxy() {
        return proxy;
    }
    
    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
        proxy.preInit(logger, event);
    }
    
    @EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
    }
    
    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }
}
