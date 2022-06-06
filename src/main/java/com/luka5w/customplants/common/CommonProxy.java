package com.luka5w.customplants.common;

import com.luka5w.customplants.common.data.MainConfig;
import com.luka5w.customplants.common.data.plantspacks.PlantsPackHandler;
import com.luka5w.customplants.common.util.Registry;
import com.luka5w.customplants.common.util.UpdateChecker;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;

import java.io.File;

public class CommonProxy {
    
    private static CommonProxy instance;
    
    protected Logger logger;
    protected MainConfig config;
    protected PlantsPackHandler plantsPackHandler;
    protected Registry registry;
    
    public void preInit(Logger logger, FMLPreInitializationEvent event) {
        instance = this;
        this.logger = logger;
        this.config = new MainConfig();
        this.registry = new Registry(this.logger);
        // (Client-)PlantsPackHandler::new; See JavaDoc for more info
        this.initPlantsPackHandler();
        this.plantsPackHandler.loadPacks();
    }
    
    public void init(FMLInitializationEvent event) {
    
    }
    
    public void postInit(FMLPostInitializationEvent event) {
        UpdateChecker.init(logger);
    }
    
    /**
     * This method initializes the PlantsPackHandler.
     * It will be overwritten in the ClientProxy to use the ClientPlantsPackHandler which loads the ResourcePack too.
     */
    protected void initPlantsPackHandler() {
        this.plantsPackHandler = new PlantsPackHandler(this.logger, new File(".", MainConfig.dirPlantsPacks), this.registry, MainConfig.Performance.maxDrops);
    }
}
