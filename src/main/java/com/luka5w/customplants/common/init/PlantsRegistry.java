package com.luka5w.customplants.common.init;

import com.luka5w.customplants.common.blocks.*;
import com.luka5w.customplants.common.data.ConfigException;
import com.luka5w.customplants.common.data.Plant;
import com.luka5w.customplants.common.data.ResourceConfigHandler;
import com.luka5w.customplants.common.data.TreeConfig;
import com.luka5w.customplants.common.items.ItemCustom;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;

public class PlantsRegistry {
    
    private final ResourceConfigHandler resourceConfigHandler;
    private final Logger logger;
    
    /** All custom blocks. */
    private final ArrayList<BlockCustomPlant> blocks;
    
    /** All custom items from custom blocks. */
    private final ArrayList<ItemBlock> itemblocks;
    
    /** All custom items. */
    private final ArrayList<ItemCustom> items;
    
    /** All custom trees. */
    private ArrayList<TreeConfig> treeConfigs;
    
    public PlantsRegistry(Logger logger, ResourceConfigHandler resourceConfigHandler) {
        this.logger = logger;
        this.resourceConfigHandler = resourceConfigHandler;
    
        Iterator<Plant> iterator = this.resourceConfigHandler.getPlants();
        this.blocks = new ArrayList<>();
        this.items = new ArrayList<>();
        this.itemblocks = new ArrayList<>();
        this.treeConfigs = new ArrayList<>();
        while (iterator.hasNext()) {
            Plant plant = iterator.next();
            try {
                this.parsePlant(plant);
            }
            catch (ConfigException e) {
                this.logger.error("Error while parsing {}: {}", plant.getName(), e.getMessage());
            }
        }
        
        MinecraftForge.EVENT_BUS.register(this);
    }
    
    private void parsePlant(Plant plant) throws ConfigException {
        switch(plant.getType()) {
            case BUSH:
                BlockCustomBush bush = plant.createBush();
                this.blocks.add(bush);
                this.itemblocks.add(new ItemBlock(bush, CreativeTab.getInstance()));
                break;
            case CROP:
                BlockCustomCrops crop = plant.createCrop();
                this.blocks.add(crop);
                if (crop.isPickupAllowed()) this.itemblocks.add(new ItemBlock(crop, CreativeTab.getInstance()));
                this.items.add(plant.createSeeds());
                break;
            case EXTENDABLE:
                BlockCustomExtendable extendable = plant.createExtendable();
                this.blocks.add(extendable);
                this.itemblocks.add(new ItemBlock(extendable, CreativeTab.getInstance()));
                break;
            case OVERLAY:
                BlockCustomOverlay overlay = plant.createOverlay();
                this.blocks.add(overlay);
                this.itemblocks.add(new ItemBlock(overlay, CreativeTab.getInstance()));
                break;
            case TREE:
                BlockCustomSapling sapling = plant.createSapling();
                this.blocks.add(sapling);
                this.itemblocks.add(new ItemBlock(sapling, CreativeTab.getInstance()));
                this.treeConfigs.add(plant.createTree());
            default:
                throw new IllegalStateException();
        }
    }
    
    @SubscribeEvent
    public void registerBlocks(RegistryEvent.Register<Block> event) {
        IForgeRegistry<Block> registry = event.getRegistry();
        this.blocks.forEach(registry::register);
    }
    
    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> event) {
        IForgeRegistry<Item> registry = event.getRegistry();
        this.items.forEach(registry::register);
        this.itemblocks.forEach(registry::register);
    }
    
    /**
     * @return All custom blocks
     */
    public ArrayList<BlockCustomPlant> getBlocks() {
        return this.blocks;
    }
    
    /**
     * @param clazz The class
     * @return The first block which is an instance of the passed class
     */
    @Nullable
    public BlockCustomPlant getBlock(Class<? extends BlockCustomPlant> clazz) {
        return this.blocks.stream().filter(it -> it.getClass().equals(clazz)).findFirst().orElse(null);
    }
    
    /**
     * @return All custom itemblocks
     */
    public ArrayList<ItemBlock> getItemBlocks() {
        return this.itemblocks;
    }
    
    /**
     * @param clazz The class
     * @return The first itemblock which is an instance of the passed class
     */
    @Nullable
    public ItemBlock getItemBlock(Class<? extends ItemBlock> clazz) {
        return this.itemblocks.stream().filter(it -> it.getClass().equals(clazz)).findFirst().orElse(null);
    }
    
    /**
     * @return All custom items
     */
    public ArrayList<ItemCustom> getItems() {
        return this.items;
    }
    
    /**
     * @param clazz The class
     * @return The first item which is an instance of the passed class
     */
    @Nullable
    public ItemCustom getItem(Class<? extends ItemCustom> clazz) {
        return this.items.stream().filter(it -> it.getClass().equals(clazz)).findFirst().orElse(null);
    }
    
    /**
     * @return All custom plants
     */
    public Iterator<Plant> getPlants() {
        return this.resourceConfigHandler.getPlants();
    }
}
