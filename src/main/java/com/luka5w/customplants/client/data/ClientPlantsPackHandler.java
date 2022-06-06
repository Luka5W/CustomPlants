package com.luka5w.customplants.client.data;

import com.luka5w.customplants.common.data.plantspacks.PlantsPackHandler;
import com.luka5w.customplants.common.util.Registry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.FileResourcePack;
import net.minecraft.client.resources.FolderResourcePack;
import net.minecraft.client.resources.IResourcePack;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ClientPlantsPackHandler extends PlantsPackHandler {
    
    private List<IResourcePack> defaultResourcePacks;
    
    public ClientPlantsPackHandler(Logger logger, File packsDir, Registry registry, int maxDrops) {
        super(logger, packsDir, registry, maxDrops);
        this.defaultResourcePacks = null;
    }
    
    @Override
    protected void registerResourcePack(File pack) {
        if (this.defaultResourcePacks == null) this.getDefaultResourcePacks();
        this.defaultResourcePacks.add(pack.isDirectory() ? new FolderResourcePack(pack) : new FileResourcePack(pack));
    }
    
    // currently unused - will get interesting when packs can be enabled/ disabled without restarting
    /*private void unregisterResourcePack(File pack) {
        if (this.defaultResourcePacks == null) return;
        Iterator<IResourcePack> it = this.defaultResourcePacks.iterator();
        while (it.hasNext()) {
            IResourcePack resourcePack = it.next();
            if (!(resourcePack instanceof AbstractResourcePack)) continue;
            File packFile = ObfuscationReflectionHelper.getPrivateValue(AbstractResourcePack.class, (AbstractResourcePack)resourcePack, "field_110597_b", "resourcePackFile");
            if (!Objects.equals(packFile, pack)) continue;
            it.remove();
            break;
        }
    }*/
    
    private void getDefaultResourcePacks() {
        try {
            this.defaultResourcePacks = ObfuscationReflectionHelper.getPrivateValue(FMLClientHandler.class, FMLClientHandler.instance(), "resourcePackList");
        }
        catch (Exception e) {
            this.logger.warn("Caught exception while trying to load resource pack list. =( The addon resource packs aren't going to load!");
            this.defaultResourcePacks = new ArrayList();
        }
    }
    
    @Override
    protected void refreshResources() {
        // reloads everything
        Minecraft.getMinecraft().refreshResources();
        // TODO: 25.04.22 what does {@link FMLClientHelper#refreshResources} need for reloading a resource pack???
        //  - i guess: sounds, langs, models
        //  - i wonder: textures
        // should(tm) reload only passed stuff BUT throws IllegalStateException;
        // See {@link SelectiveReloadStateHandler#beginReload}:
        // > [...] Should only be called when initiating a resource reload. If a reload is already in progress when this
        // > is called, an exception will be thrown.
        /*FMLClientHandler.instance().refreshResources(VanillaResourceType.LANGUAGES,
                                                     VanillaResourceType.MODELS,
                                                     VanillaResourceType.SOUNDS,
                                                     VanillaResourceType.TEXTURES);*/
    }
}
