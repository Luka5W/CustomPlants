package com.luka5w.customplants.client;

import com.luka5w.customplants.client.data.ClientPlantsPackHandler;
import com.luka5w.customplants.client.util.ModelRegistry;
import com.luka5w.customplants.common.CommonProxy;
import com.luka5w.customplants.common.data.MainConfig;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;

import java.io.File;

public class ClientProxy extends CommonProxy {
    @Override
    public void preInit(Logger logger, FMLPreInitializationEvent event) {
        super.preInit(logger, event);
        ModelRegistry.init(this.logger);
    }
    
    @Override
    protected void initPlantsPackHandler() {
        this.plantsPackHandler = new ClientPlantsPackHandler(this.logger, new File(Minecraft.getMinecraft().mcDataDir, MainConfig.dirPlantPacks), this.registry);
    }
}
