package com.luka5w.customplants.common.util;

import com.luka5w.customplants.CustomPlants;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDeadBush;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.registries.IForgeRegistry;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Objects;

@Mod.EventBusSubscriber(modid = CustomPlants.MOD_ID)
public class Registry {
    
    private static Registry instance;
    
    private final Logger logger;
    private final ArrayList<Tuple<Class<? extends TileEntity>, ResourceLocation>> tileEntities;
    private final ArrayList<Block> blocks;
    private final ArrayList<ItemBlock> itemBlocks;
    private final ArrayList<Item> items;
    private final ArrayList<Tuple<String, Item>> oresI;
    private final ArrayList<Tuple<String, Block>> oresB;
    private final ArrayList<CreativeTabs> tabs;
    
    public static Registry getInstance() {
        return instance;
    }
    
    public Registry(Logger logger) {
        instance = this;
        this.logger = logger;
        this.tileEntities = new ArrayList<>();
        this.blocks = new ArrayList<>();
        this.itemBlocks = new ArrayList<>();
        this.items = new ArrayList<>();
        this.oresI = new ArrayList<>();
        this.oresB = new ArrayList<>();
        this.tabs = new ArrayList<>();
    
        // TODO: 01.05.22 is generation prevented?
        Block iconBlock = new BlockDeadBush() {};
        this.addBlock(iconBlock, "customplant", true);
        this.addTab(new ItemStack(this.itemBlocks.get(0)), CustomPlants.MOD_ID);
        iconBlock.setCreativeTab(this.tabs.get(0));
    }
    
    public Registry addTileEntity(Class<? extends TileEntity> tileEntityClass, String resourcePath) {
        this.tileEntities.add(new Tuple(tileEntityClass, new ResourceLocation(CustomPlants.MOD_ID, resourcePath)));
        return this;
    }
    
    public ArrayList<Tuple<Class<? extends TileEntity>, ResourceLocation>> getTileEntities() {
        return this.tileEntities;
    }
    
    public Registry addBlock(Block block, String resourcePath, boolean registerAsItem, CreativeTabs tab) {
        block.setCreativeTab(tab);
        return this.addBlock(block, resourcePath, registerAsItem);
    }
    
    public Registry addBlock(Block block, String resourcePath, boolean registerAsItem) {
        block.setUnlocalizedName(resourcePath);
        if (block.getRegistryName() == null) block.setRegistryName(CustomPlants.MOD_ID, resourcePath);
        if (registerAsItem) this.addItemBlock(new ItemBlock(block), resourcePath);
        this.blocks.add(block);
        return this;
    }
    
    @Nullable
    public Block getBlock(String resourcePath) {
        return this.blocks.stream().filter(it -> Objects.equals(it.getRegistryName().getResourcePath(), resourcePath)).findFirst().orElse(null);
    }
    
    public ArrayList<Block> getBlocks() {
        return this.blocks;
    }
    
    public Registry addItemBlock(ItemBlock itemBlock, String resourcePath) {
        if (itemBlock.getRegistryName() == null) itemBlock.setRegistryName(CustomPlants.MOD_ID, resourcePath);
        this.itemBlocks.add(itemBlock);
        return this;
    }
    
    @Nullable
    public ItemBlock getItemBlock(String resourcePath) {
        return this.itemBlocks.stream().filter(it -> Objects.equals(it.getRegistryName().getResourcePath(), resourcePath)).findFirst().orElse(null);
    }
    
    public ArrayList<ItemBlock> getItemBlocks() {
        return this.itemBlocks;
    }
    
    public Registry addItem(Item item, String resourcePath, CreativeTabs tab) {
        item.setCreativeTab(tab);
        return this.addItem(item, resourcePath);
    }
    
    public Registry addItem(Item item, String resourcePath) {
        item.setUnlocalizedName(resourcePath);
        if (item.getRegistryName() == null) item.setRegistryName(CustomPlants.MOD_ID, resourcePath);
        this.items.add(item);
        return this;
    }
    
    @Nullable
    public Item getItem(String resourcePath) {
        return this.items.stream().filter(it -> Objects.equals(it.getRegistryName().getResourcePath(), resourcePath)).findFirst().orElse(null);
    }
    
    public ArrayList<Item> getItems() {
        return this.items;
    }
    
    public Registry addOre(String name, Item ore) {
        this.oresI.add(new Tuple(name, ore));
        return this;
    }
    
    public Registry addOre(String name, Block ore) {
        this.oresB.add(new Tuple(name, ore));
        return this;
    }
    
    public Registry addTab(CreativeTabs tab) {
        this.tabs.add(tab);
        return this;
    }
    
    public Registry addTab(ItemStack icon, String label) {
        this.tabs.add(new CreativeTabs(label) {
            @Override
            public ItemStack getTabIconItem() {
                return icon;
            }
        });
        return this;
    }
    public Registry addTab(ItemStack icon, int index, String label) {
        this.tabs.add(new CreativeTabs(index, label) {
            @Override
            public ItemStack getTabIconItem() {
                return icon;
            }
        });
        return this;
    }
    
    @Nullable
    public CreativeTabs getTab(String label) {
        return this.tabs.stream().filter(it -> Objects.equals(it.getTabLabel(), label)).findFirst().orElse(null);
    }
    
    public CreativeTabs getDefaultTab() {
        return this.tabs.get(0);
    }
    
    public ArrayList<CreativeTabs> getTabs() {
        return this.tabs;
    }
    
    public static void registerTileEntities() {
        for (Tuple<Class<? extends TileEntity>, ResourceLocation> te : getInstance().getTileEntities()) {
            Registry.getInstance().logger.debug("Registering TileEntity '{}'", te.getSecond());
            GameRegistry.registerTileEntity(te.getFirst(), te.getSecond());
        }
    }
    
    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        IForgeRegistry<Block> registry = event.getRegistry();
        for (Block block : getInstance().getBlocks()) {
            Registry.getInstance().logger.debug("Registering Block '{}'", block.getRegistryName());
            registry.register(block);
        }
        registerTileEntities();
    }
    
    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        IForgeRegistry<Item> registry = event.getRegistry();
        for (Item item : getInstance().getItems()) {
            getInstance().logger.debug("Registering Item '{}'", item.getRegistryName());
            registry.register(item);
        }
        for (ItemBlock itemBlock : getInstance().getItemBlocks()) {
            getInstance().logger.debug("Registering ItemBlock '{}'", itemBlock.getRegistryName());
            registry.register(itemBlock);
        }
    }
    
    @SubscribeEvent
    public static void registerOres(OreDictionary.OreRegisterEvent event) {
        for (Tuple<String, Item> ore : getInstance().oresI)
            OreDictionary.registerOre(ore.getFirst(), ore.getSecond());
        for (Tuple<String, Block> ore : getInstance().oresB)
            OreDictionary.registerOre(ore.getFirst(), ore.getSecond());
    }
}
