package com.luka5w.customplants.common;

import com.luka5w.customplants.common.data.MainConfig;
import com.luka5w.customplants.common.data.ResourceConfigHandler;
import com.luka5w.customplants.common.init.PlantsRegistry;
import com.luka5w.customplants.common.util.UpdateChecker;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;

public class CommonProxy {
    
    private static CommonProxy instance;
    private Logger logger;
    private MainConfig config;
    protected ResourceConfigHandler resourceConfigHandler;
    protected PlantsRegistry plantsRegistry;
    
    public void preInit(Logger logger, FMLPreInitializationEvent event) {
        instance = this;
        this.logger = logger;
        this.config = new MainConfig();
    
        UpdateChecker.init(logger);
        
        this.resourceConfigHandler = new ResourceConfigHandler(logger);
        this.plantsRegistry = new PlantsRegistry(this.logger, this.resourceConfigHandler);
    }
    
    public void init(FMLInitializationEvent event) {
    
    }
    
    public void postInit(FMLPostInitializationEvent event) {
    
    }
    
    public String getTranslation(@Nonnull String key) {
        return this.resourceConfigHandler.getTranslation("en_us", null, key);
    }
}
