package com.luka5w.customplants.client;

import com.luka5w.customplants.client.init.ModelRegistry;
import com.luka5w.customplants.common.CommonProxy;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;

public class ClientProxy extends CommonProxy {
    @Override
    public void preInit(Logger logger, FMLPreInitializationEvent event) {
        super.preInit(logger, event);
        new ModelRegistry(this.plantsRegistry);
    }
    
    @Override
    public String getTranslation(@Nonnull String key) {
        return this.resourceConfigHandler.getTranslation(Minecraft.getMinecraft().getLanguageManager().getCurrentLanguage().getLanguageCode(), "en_us", key);
    }
}
